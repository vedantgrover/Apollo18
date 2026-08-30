# Apollo18

<hr>
<a href="https://top.gg/bot/853812538218381352">
  <img src="https://top.gg/api/widget/853812538218381352.svg">
</a>
<hr>

Apollo18 is a multipurpose Discord bot built with [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA) and a MongoDB backend. It covers economy, casino games, leveling, business/stock simulation, image manipulation, utility commands, and more.

### Built With

* [Java Discord API (JDA)](https://github.com/DV8FromTheWorld/JDA)
* [MongoDB Java Driver](https://www.mongodb.com/docs/drivers/java/sync/current/)
* [DotEnv](https://github.com/cdimascio/dotenv-java)
* [LavaPlayer](https://github.com/sedmelluq/lavaplayer)
* [JFreeChart](https://www.jfree.org/jfreechart/) (stock chart generation)

<p align="right">(<a href="#apollo18">back to top</a>)</p>

## Self Hosting

To get a local copy of Apollo18 up and running, follow these simple steps.

### Prerequisites

You must have [Java JDK 26](https://www.oracle.com/java/technologies/downloads/) installed on your machine, or [Docker](https://docs.docker.com/get-docker/) with the Compose plugin.

### Installation

_Below are the necessary steps needed to run this project._

1. Clone the repo:
   ```sh
   git clone https://github.com/vedantgrover/Apollo18.git
   ```
2. Create a `.env` file in the root project folder and populate it with the variables in `.env.example`. Only `TOKEN` plus a Mongo target (`MONGODB` or `MONGO_URI`) are required to boot; every other key just degrades a single command if left blank.
3. Build and run with Docker Compose (recommended):
   ```sh
   docker compose up -d --build
   ```
   Or build and run with Maven directly:
   ```sh
   mvn -B package
   java -jar target/Apollo18-1.0.0.jar
   ```

`MONGO_URI` accepts a complete MongoDB connection string and overrides `MONGODB` when set.

### Running Tests

There is currently no test suite in this repo (`mvn test` has nothing to run).

<p align="right">(<a href="#apollo18">back to top</a>)</p>

## Project Structure

* `commands/` — slash commands, organized by category (`business`, `casino`, `dev`, `economy`, `fun`, `image`, `information`, `leveling`, `music`, `settings`, `utility`). New commands are registered in `CommandManager`.
* `data/` — MongoDB access layer (`data/Database.java`) and domain records (`data/records/**`) with matching codecs.
* `listeners/` — JDA event listeners (guild join/leave, buttons, leveling XP, bot lifecycle).
* `handlers/` — cross-cutting logic shared across multiple commands.
* `util/` — embed styling, music/LavaPlayer wiring, text formatters, and API helpers.

See [`CLAUDE.md`](CLAUDE.md) for a more detailed architecture overview.

<p align="right">(<a href="#apollo18">back to top</a>)</p>

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request — it covers our etiquette expectations and workflow. If you have a suggestion, you can also open an issue with the tag "enhancement".

Don't forget to give the project a star! Thanks again!

<p align="right">(<a href="#apollo18">back to top</a>)</p>

<!-- CONTACT -->
## Contact

Vedant Grover - bladedurman@gmail.com

Project Link: [https://github.com/vedantgrover/Apollo18](https://github.com/vedantgrover/Apollo18)

<p align="right">(<a href="#apollo18">back to top</a>)</p>
