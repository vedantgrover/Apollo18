# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Apollo18 is a multipurpose Discord bot written in Java 26, built on JDA (Java Discord API) with a MongoDB backend. Package root: `com.freyr.apollo18`.

## Build & Run

- Build: `mvn -B package` (runs the `maven-shade-plugin` to produce a shaded/fat jar with `com.freyr.apollo18.Apollo18` as the main class)
- Run locally: `java -jar target/Apollo18-1.0.0.jar` after building, or run `Apollo18.main` directly from an IDE
- Docker: `docker compose up -d --build` builds the image (multi-stage `Dockerfile`, Maven build stage + `eclipse-temurin:26-jre-noble` runtime) and runs it via `docker-compose.yml`. Secrets are supplied via `env_file: .env` at run time, never baked into the image.
- No test suite currently exists in this repo (`mvn test` has nothing to run)
- CI: `.github/workflows/maven.yml` runs `mvn -B package` on push/PR to `main` (JDK 26, Oracle OpenJDK)

### Configuration

The bot loads secrets via `dotenv-java` from a `.env` file in the project root (see `.env.example` for the full list: `TOKEN`, `MONGODB`, `MONGO_URI`, `WEATHER_TOKEN`, `TOPGG_TOKEN`, `YOUTUBE_TOKEN`, `RAPIDAPI_KEY`, `ALPHAVANTAGE`, `DICTIONARYAPI`, `AZURETRANSLATORKEY`, `OPENAI_KEY`, etc). Every lookup falls back to a real environment variable via `config.get("KEY", System.getenv("KEY"))`, so in production (e.g. containers) these can be supplied as env vars with no `.env` file present. `Apollo18` checks `TOKEN` before constructing `Database`, and exits on startup if it's missing.

`MONGO_URI` (a complete connection string) overrides `MONGODB` when set; otherwise `Apollo18.java` falls back to the hardcoded `apollo18` Atlas cluster host with `MONGODB` supplying just the credential portion.

`STOCK_DATA_DIR` controls where `StockData`/`BusinessCommand` read and write generated stock chart PNGs. It defaults to `src/main/resources/stock_data` (IDE/local runs); the Docker image sets it to `/app/data/stock_data`, a named volume seeded from the three git-tracked charts (AAPL, MSFT, META) on first run.

`scripts/launch-apollo18.sh` is a leftover OCI (Oracle Cloud) instance-provisioning script from an earlier, abandoned deployment plan — it is untracked and unrelated to the current Docker-based deployment or the Maven build.

## Architecture

### Command system

- `commands/Command.java` — abstract base every command extends. Holds shared metadata (`name`, `description`, `category`, `devOnly`, `cooldown`, `uses`, `permission`/`botPermission`, `args`, `subCommands`) plus helper methods for making HTTP calls (`getApiData`, `postApiData` via OkHttp).
- `commands/CommandManager.java` — a `ListenerAdapter` that:
  - Registers every command instance in a static list/map (`mapCommands` at the top of its constructor — this is the single place new commands must be added)
  - Converts each `Command` into Discord `CommandData` (`unpackCommandData`) and pushes them as guild commands (`onGuildReady`) and global commands (`onReady`)
  - Handles `onSlashCommandInteraction`: dev-only gating (hardcoded Discord user ID), bot-permission checks, per-command cooldown/uses tracking, then dispatches to `cmd.execute(event)`
- Commands are organized into subpackages by `Category` (`commands/Category.java`): `business`, `casino`, `dev`, `economy`, `fun`, `image` (with `image/image` and `image/text` sub-groups), `information`, `leveling`, `music`, `settings`, `utility`.
- Music commands are currently commented out in `CommandManager`'s constructor (disabled feature).

### Data layer

- `data/Database.java` — the **only** class allowed direct access to MongoDB collections (`guildData`, `userData`, `businessData`, `transactionData`). All command/handler code must go through this class rather than touching Mongo collections directly. It's organized into `// region` blocks (Welcome System, Leveling System, Economy System, Transactions, Music, Businesses, Jobs, Notifications).
- Domain objects are Java `record`s under `data/records/**` (e.g. `records/user/User.java`, `records/business/Business.java`, `records/guild/Guild.java`), mirroring nested MongoDB document structure (a `User` embeds `UserEconomy`, `UserMusic`, a list of `UserLeveling` per guild, etc).
- Each record family that needs custom (de)serialization has a matching `data/codec/**/*Codec.java` (manual `Codec<T>` implementations) and, where the codec needs to be resolved dynamically, a `data/provider/*CodecProvider.java` registered in `Database`'s constructor. When adding a new record type stored in Mongo, you generally need: the record, a codec, and (if it's a top-level collection type or resolved polymorphically) a provider registered in `Database`.
- `data/StockData.java` handles fetching/plotting stock price history (JFreeChart) for the business/stock economy feature. `StockData.stockDataDir()` is the single source of truth for the chart output directory (see `STOCK_DATA_DIR` above) — both the writer here and the reader in `BusinessCommand` go through it so they cannot drift.

### Listeners & handlers

- `listeners/` — JDA `ListenerAdapter`s for guild join/leave (`GuildListener`), button interactions (`ButtonListener`), leveling XP gain (`LevelingListener`), and misc bot lifecycle events (`BotListener`).
- `handlers/` — cross-cutting logic used by multiple commands, e.g. `BusinessHandler` (stock arrow emoji, business helpers) and `LevelingHandler`.

### Scheduling

`Apollo18.java`'s constructor sets up a daily `ScheduledExecutorService` job (anchored to 1:01 AM America/Los_Angeles) that calls `database.updateStocks()` and `database.dailyWorkChecks()`.

### Utilities

- `util/embeds/` — `EmbedUtils`/`EmbedColor` for consistent Discord embed styling (e.g. `EmbedUtils.createError(...)`).
- `util/music/` — LavaPlayer wiring (`PlayerManager`, `GuildMusicManager`, `TrackScheduler`, `AudioPlayerSendHandler`) for the currently-disabled music feature.
- `util/textFormatters/` — number/text formatting helpers (`NumberFormatter`, `TextFormatter`, `RandomString`).
- `util/api/ImageManipulationAPI.java` — backs the `commands/image/**` meme/image-manipulation commands.

## Conventions

- New slash commands: create a class extending `Command` in the appropriate `commands/<category>` package, set `name`/`description`/`category` (and any `args`, `permission`, `cooldown`, `devOnly`) in the constructor, implement `execute`, then register the instance in `CommandManager`'s constructor.
- Database access from commands always goes through `bot.getDatabase()`, never a direct Mongo call.
- Dev-only commands live in `commands/dev/` and are gated by a hardcoded developer Discord user ID in `CommandManager.onSlashCommandInteraction`.
