# WordCheat

WordCheat is a desktop helper for Words With Friends. You copy the board and your letters into it,
and it lists every move you can play, with the highest score first.

## Background

WordCheat exists because my mother always beats me at Words With Friends.

The save file the app opens on start-up is called `mom`, so the source makes no secret of who this
was built to beat.

## Requirements

- Java 17 or later
- Maven

Maven downloads JavaFX 21 along with the other dependencies. The `pom.xml` asks for the Windows build
of JavaFX, through the `javafx.platform` property. Anyone building on macOS or Linux sets that
property to `mac` or `linux`.

## Running

Maven starts the app through the JavaFX plugin:

```
mvn javafx:run
```

## Using WordCheat

The window shows three areas, with a row of buttons underneath:

| Area       | What it shows                                                                            |
|------------|------------------------------------------------------------------------------------------|
| Board      | The 15 by 15 board, with the double and triple letter and word squares marked on it.     |
| Tile pool  | Every tile in the game, with a count of how many of each letter are still unaccounted for. |
| Rack       | Your seven letters.                                                                      |

You enter a game by dragging tiles out of the pool. Tiles dropped on the board copy the words already
played in the real game. Tiles dropped on the rack copy the letters you are holding. The pool counts
go down as you drag, so the pool also tells you which letters your opponent could still have.

The four buttons then do the following:

| Button    | Action                                                                                           |
|-----------|--------------------------------------------------------------------------------------------------|
| Commit    | Checks the board, then saves the board and rack as they appear on screen.                        |
| Best Move | Shows the highest scoring move on the board, and prints it to the console.                       |
| Next Move | Shows the next move down the list, so you can step through the alternatives.                     |
| Reset     | Puts the board and rack back to the last committed state, which clears any move being shown.     |

Each commit writes the game to `~/.wordcheat/saves/mom.json` in your home folder. The app loads that
file when it starts. As such, a game carries on from one session to the next, and you only drag in the
tiles played since the last turn.

## How moves are found

WordCheat checks words against a list of 168,556 English words, stored in
`src/main/resources/words/words_alpha.txt`. The search for a turn runs in three steps:

1. It builds every word that can be spelled from the letters on your rack.
2. It tries each word at every position that touches a tile already on the board. It also tries
   words built through those existing tiles, using your letters on either side of them.
3. It keeps a placement only where every word on the board is still in the list, including the
   crossing words it forms.

Each move is scored with the Words With Friends letter values and bonus squares. A move that uses all
seven tiles earns an extra 35 points. On an empty board, WordCheat only tries placements that cross
the centre square.

A blank tile scores nothing wherever it lands.

## Project layout

The code is split into packages under `com.slinky.wordcheat`:

| Package    | Contents                                                                      |
|------------|-------------------------------------------------------------------------------|
| `model`    | The board, rack, tile pool, scoring and move search. `GameEngine` ties them together. |
| `language` | The word list and the generator that builds words from a set of letters.      |
| `view`     | The JavaFX board, pool, rack and tiles.                                       |
| `control`  | Wiring between the view and the model, including drag and drop.               |
| `io`       | Saving and loading games as JSON, and loading fonts.                          |
| `util`     | Helpers for matrices, permutations, board symmetry and colours.               |

A class diagram of the whole project is in `uml/`:

![WordCheat class diagram](uml/WordCheatUML.png)

## Tests

The tests use JUnit 5 and cover the model, the word generator and the utilities:

```
mvn test
```
