const {exec} = require('child_process');
const { BlobServiceClient, StorageSharedKeyCredential } = require('@azure/storage-blob');
const fs = require('fs');
const Canvacord = require('canvacord');
const express = require('express');
const bodyParser = require('body-parser')
const app = express();

require('dotenv').config();

app.use(bodyParser.json());

// Azure Storage account and container details
const accountName = 'apollo18storage';
const accountKey = process.env.AZURESTORAGEACCOUNTKEY;
const containerName = 'image-container';

// Create an instance of the BlobServiceClient
const sharedKeyCredential = new StorageSharedKeyCredential(accountName, accountKey);
const blobServiceClient = new BlobServiceClient(`https://${accountName}.blob.core.windows.net`, sharedKeyCredential);

// Set the TTL for a blob
async function setBlobTTL(containerName, blobName, ttlInMinutes) {
    const containerClient = blobServiceClient.getContainerClient(containerName);
    const blockBlobClient = containerClient.getBlockBlobClient(blobName);

    // Calculate the expiration date based on the TTL
    const expirationDate = new Date(Date.now() + ttlInMinutes * 1000);

    // Set the TTL as metadata
    const metadata = {
        ttl: expirationDate.toISOString(), // Store the expiration date as metadata
    };

    // Set the blob metadata
    await blockBlobClient.setMetadata(metadata);

    console.log(`TTL set for blob "${blobName}" in container "${containerName}"`);
}

// Generate an image using Canvacord, save it locally, and upload it to Azure Blob Storage
async function generateAndUploadImage_Image(url, url2 = "https://ih1.redbubble.net/image.1288426316.2422/flat,750x,075,f-pad,750x1000,f8f8f8.jpg", manipulation) {
    let image;
    let imageExt = ".png";

    switch (manipulation) {
        case "trigger":
            image = await Canvacord.Canvas.trigger(url);
            imageExt = ".gif";
            break;
        case "beautiful":
            image = await Canvacord.Canvas.beautiful(url);
            break;
        case "burn":
            image = await Canvacord.Canvas.burn(url);
            break;
        case "distracted":
            image = await Canvacord.Canvas.distracted(url, url2);
            break;
        case "facepalm":
            image = await Canvacord.Canvas.facepalm(url);
            break;
        case "fuse":
            image = await Canvacord.Canvas.fuse(url, url2);
            break;
        case "hitler":
            image = await Canvacord.Canvas.hitler(url);
            break;
        case "invert":
            image = await Canvacord.Canvas.invert(url);
            break;
        case "jail":
            image = await Canvacord.Canvas.jail(url);
            break;
        case "jokeoverhead":
            image = await Canvacord.Canvas.jokeOverHead(url);
            break;
        case "rainbow":
            image = await Canvacord.Canvas.rainbow(url);
            break;
        case "rip":
            image = await Canvacord.Canvas.rip(url);
            break;
        case "slap":
            image = await Canvacord.Canvas.slap(url, url2);
            break;
        case "spank":
            image = await Canvacord.Canvas.spank(url, url2);
            break;
        case "wanted":
            image = await Canvacord.Canvas.wanted(url);
            break;

    }

    const containerClient = blobServiceClient.getContainerClient(containerName);
    const blobName = `image_${Date.now()}${imageExt}`;

    // Save the image buffer locally (optional)
    fs.writeFileSync(blobName, image);

    // Upload the image to Azure Blob Storage
    const blockBlobClient = containerClient.getBlockBlobClient(blobName);
    await blockBlobClient.uploadFile(blobName);

    setBlobTTL(containerName, blobName, 15);
    console.log("TTL Set successfully");

    console.log('Image uploaded successfully.');

    // Generate the URL for the uploaded image
    const blobUrl = blockBlobClient.url;

    console.log('Image URL:', blobUrl);

    // Delete the locally saved image (optional)
    fs.unlinkSync(blobName);

    return blobUrl;
}



app.post("/image", async (req, res) => {
    let reqBody = req.body;
    console.log(reqBody)

    let url1 = reqBody.url1;
    let url2 = reqBody.url2 || undefined;
    let manipulation = reqBody.manipulation;

    let url = await generateAndUploadImage_Image(url1, url2, manipulation);

    res.json({ url: url });
});

console.log("Running Shell Script")
exec('sh ~/Scripts/restart_bot.sh', (error, stdout, stderr) => { });

app.post('/webhook', (req, res) => {
    // Execute the shell script
    exec('sh ~/Scripts/restart_bot.sh', (error, stdout, stderr) => {
        if (error) {
            console.error(`Error executing script: ${error.message}`);
            return res.status(500).send('Internal Server Error');
        }
        if (stderr) {
            console.error(`Script stderr: ${stderr}`);
        }
        console.log(`Script stdout: ${stdout}`);
        res.sendStatus(200);
    });
});

app.get('/', (req, res) => {
    res.send("Hello World!");
});

app.listen(3000, () => console.log("Listening on 3000"));