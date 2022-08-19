# Contributing

:+1: First off, thanks for taking the time to contribute! :+1:

# Contributor License Agreement (CLA)

A CLA is a document that specifies how a project is allowed to use your contribution.  We want a CLA that is simple and as clear as possible so that it doesn't impede contributions to the project.

When you make a contribution to the project, you agree:

1. Your contribution is your original work (you own the copyright) or you otherwise have the right to submit the work.
2. You grant the project a nonexclusive, irrevocable license to use your submitted contribution in any way.
3. You are capable of granting these rights for the contribution.

**By submitting a contribution to the project you agree to the above statements.**

# Contributing Issues

## Prerequisites

* [ ] Have you [searched for duplicates](https://github.com/pmonks/spinner/issues?utf8=%E2%9C%93&q=)?  A simple search for exception error messages or a summary of the unexpected behaviour should suffice.
* [ ] Are you sure this is a bug or missing capability?

## Raising an Issue

* Create your issue [here](https://github.com/pmonks/spinner/issues/new).
* New issues contain two templates in the description: bug report and enhancement request. Please pick the most appropriate for your issue.
* Please use [Markdown formatting](https://help.github.com/categories/writing-on-github/) liberally to assist in readability.
  * [Code fences](https://help.github.com/articles/creating-and-highlighting-code-blocks/) for exception stack traces and log entries, for example, massively improve readability.

# Contributing Pull Requests (Code & Docs)

To make review of PRs easier, please:

 * Reference an issue from your PR.  If there isn't an existing issue for your PR, please create an issue first before submitting the PR.
   * This helps expedite review by keeping the problem statement (the issue) explicitly separate from one of potentially many solutions (the PR).
 * Make sure your PRs will merge cleanly - PRs that don't are unlikely to be accepted.
 * For code contributions, follow the existing coding style.
 * For documentation contributions, follow the general structure, language, and tone of the existing docs.
 * Keep PRs small and cohesive - if you have multiple independent contributions, please submit them as independent PRs.
 * Minimise "spurious" changes (e.g. whitespace shenanigans).
 * Ensure all updated files include your copyright information at the top.
 * Ensure all new files include a header comment block containing the [Apache 2.0 license header and your copyright information](https://infra.apache.org/apply-license.html).

## Commit and PR Messages

* **Reference issues, wiki pages, and pull requests liberally!**
* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move button left..." not "Moves button left...")
* Limit the first line to 72 characters or less
* Please start the commit message with one or more applicable emoji:

| Emoji | Raw Emoji Code | Description |
|:---:|:---:|---|
| :tada: | `:tada:` | **initial** commit |
| :construction: | `:construction:` | **WIP** (Work In Progress) commits |
| :ambulance: | `:ambulance:` | when fixing a **bug** |
| :bug: | `:bug:` | when **identifying a bug**, via an inline comment (please use the `@FIXME` tag in the comment) |
| :new: | `:new:` | when introducing **new** features |
| :art: | `:art:` | when improving the **format** / structure of the code |
| :pencil: | `:pencil:` | when **performing minor changes / fixing** the code or language |
| :ballot_box_with_check: | `:ballot_box_with_check:` | when completing a task |
| :arrow_up: | `:arrow_up:` | when upgrading **dependencies** |
| :arrow_down: | `:arrow_down:` | when downgrading **dependencies** |
| :racehorse: | `:racehorse:` | when improving **performance** |
| :fire: | `:fire:` | when **removing code** or files |
| :speaker: | `:speaker:` | when adding **logging** |
| :mute: | `:mute:` | when reducing **logging** |
| :books: | `:books:` | when writing **docs** |
| :bookmark: | `:bookmark:` | when adding a **tag** |
| :gem: | `:gem:` | new **release** |
| :zap: | `:zap:` | when introducing **backward incompatible** changes or **removing functionality** |
| :bulb: | `:bulb:` | new **idea** identified in the code, via an inline comment (please use the `@IDEA` tag in the comment) |
| :snowflake: | `:snowflake:` | changing **configuration** |
| :lipstick: | `:lipstick:` | when improving **UI** / cosmetic |
| :umbrella: | `:umbrella:` | when adding **tests** |
| :green_heart: | `:green_heart:` | when fixing the **CI** build |
| :lock: | `:lock:` | when dealing with **security** |
| :shirt: | `:shirt:` | when removing **linter** / strict / deprecation / reflection warnings |
| :fast_forward: | `:fast_forward:` | when **forward-porting features** from an older version/branch |
| :rewind: | `:rewind:` | when **backporting features** from a newer version/branch |
| :wheelchair: | `:wheelchair:` | when improving **accessibility** |
| :globe_with_meridians: | `:globe_with_meridians:` | when dealing with **globalisation** / internationalisation |
| :rocket: | `:rocket:` | anything related to deployments / **DevOps** |
| :non-potable_water: | `:non-potable_water:` | when plugging memory leaks
| :balance_scale: | `:balance_scale:` | when making legal changes (e.g. licensing) |
| :penguin: | `:penguin:` | when fixing something on **Linux** |
| :apple: | `:apple:` | when fixing something on **Mac OS** |
| :checkered_flag: | `:checkered_flag:` | when fixing something on **Windows** |
| :handbag: | `:handbag:` | when a commit contains multiple unrelated changes that don't fit into any one category (but please try not to do this!) |
