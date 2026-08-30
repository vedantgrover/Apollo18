# Contributing to Apollo18

First off, thanks for taking the time to contribute! Apollo18 is a community project, and contributions of any size (a typo fix, a bug report, a brand new command) are welcome.

## Code of Conduct

Be respectful. Disagreements about code and design are normal and healthy, but keep feedback focused on the work, not the person. Harassment, personal attacks, and discriminatory language or behavior will not be tolerated.

## Before You Start

- **Search existing issues and pull requests** before opening a new one. Someone may have already reported the bug or proposed the feature.
- **Open an issue before starting large work.** If you're planning something more involved than a small fix (a new command category, a refactor, a new dependency), open an issue first to discuss the approach. This avoids wasted effort if the maintainers want a different direction.
- **Keep pull requests focused.** One feature or fix per PR. Unrelated changes (formatting sweeps, unrelated refactors) make review harder and are more likely to be rejected.

## Reporting Bugs

When filing a bug report, please include:

- Steps to reproduce the issue
- What you expected to happen vs. what actually happened
- Relevant logs or error messages
- Your environment (Docker vs. local Maven run, Java version, etc.) if relevant

## Suggesting Features

Open an issue tagged `enhancement` describing:

- The problem the feature solves
- How you imagine it working (e.g. command name, arguments, behavior)
- Any alternatives you considered

## Making Changes

1. **Fork the repo** and create your branch from `main`:
   ```sh
   git checkout -b feature/your-feature-name
   ```
2. Follow the existing project conventions (see [`CLAUDE.md`](CLAUDE.md) for architecture and patterns):
   - New slash commands extend `Command` in the appropriate `commands/<category>` package and are registered in `CommandManager`.
   - All database access goes through `bot.getDatabase()` — never touch MongoDB collections directly from a command.
   - Dev-only commands live in `commands/dev/`.
3. **Never commit secrets.** Don't commit your `.env` file, tokens, or credentials. Use `.env.example` to document new configuration keys without real values.
4. **Build before you push:**
   ```sh
   mvn -B package
   ```
   There's currently no automated test suite, so manually verify your change works (e.g. run the bot locally and exercise the affected command).
5. Write clear commit messages that explain *why* a change was made, not just what changed.
6. Push to your fork and open a pull request against `main` using the [pull request template](.github/PULL_REQUEST_TEMPLATE.md).

## Pull Request Etiquette

- Keep the PR title short and descriptive.
- Fill out the PR template completely, including a test plan — even if it's just "ran the bot locally and tested `/command`".
- Be patient during review. Maintainers may ask for changes; that's a normal part of the process, not a rejection.
- Don't force-push over review history mid-conversation without a heads-up — it makes it harder to follow what changed in response to feedback.
- If your PR sits idle after requested changes, a friendly follow-up is fine, but avoid repeated pings in a short window.
- Once approved, a maintainer will merge your PR. You don't need to merge it yourself.

## Style Notes

- Match the existing code style in the file/package you're editing rather than introducing a new convention.
- Prefer small, readable methods over clever one-liners.
- Add comments only where the *why* isn't obvious from the code itself.

## Questions?

If anything here is unclear, open an issue or start a discussion — we're happy to help new contributors get oriented.

Thanks again for contributing!
