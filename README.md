# Yahtzee Game 🎲

Welcome to the LibGDX Yahtzee game! This project allows you to experience the classic dice game Yahtzee in a digital format using the LibGDX framework. Whether you're a seasoned Yahtzee player or new to the game, this implementation provides an engaging and interactive experience.

## Table of Contents

- [Game Dynamics](#game-dynamics)
- [Game Mechanics](#game-mechanics)
- [Game Elements](#game-elements)
- [How to Play](#how-to-play)
- [Installation](#installation)
- [Contributing](#contributing)
- [License](#license)

## Game Dynamics

The dynamics of the Yahtzee game are designed to capture the essence of the classic tabletop game. Players take turns rolling a set of five dice with the goal of achieving specific combinations to earn points. The strategic element comes into play when deciding which combinations to target and when to reroll the dice.

## Game Mechanics

### Dice Rolling
- Players roll five dice in each turn.
- After the initial roll, they can choose to reroll any number of dice up to two more times.
- The final combination after three rolls is used to score points.

## Scoring Combinations
- The game includes standard Yahtzee scoring combinations such as three of a kind, four of a kind, full house, small straight, large straight, and Yahtzee (five of a kind).
- Players can also score points in the upper section by accumulating the sum of a specific number (1 to 6) multiplied by the number of dice showing that number.

- Here are the scoring combinations available in the Yahtzee game:

| Combination         | Description                                           | Example                    |
|---------------------|-------------------------------------------------------|----------------------------|
| Ones                | Sum of all dice showing the number 1                 | 1 1 1 4 5 → 3 points       |
| Twos                | Sum of all dice showing the number 2                 | 2 2 2 3 6 → 6 points       |
| Threes              | Sum of all dice showing the number 3               | 3 3 3 3 2 → 12 points      |
| Fours               | Sum of all dice showing the number 4                 | 4 4 1 4 5 → 12 points      |
| Fives               | Sum of all dice showing the number 5                 | 5 5 5 2 1 → 15 points      |
| Sixes               | Sum of all dice showing the number 6                 | 6 6 4 2 6 → 18 points      |
| Three of a Kind     | At least three dice showing the same number          | 3 3 3 4 5 → Sum of all dice|
| Four of a Kind      | At least four dice showing the same number           | 2 2 2 2 6 → Sum of all dice|
| Full House          | Three of a kind and a pair                           | 1 1 3 3 3 → 25 points      |
| Small Straight      | Four sequential dice (1-2-3-4 or 2-3-4-5 or 3-4-5-6)| 1 2 3 4 6 → 30 points      |
| Large Straight      | Five sequential dice (1-2-3-4-5 or 2-3-4-5-6)       | 2 3 4 5 6 → 40 points      |
| Yahtzee             | Five of a kind                                       | 6 6 6 6 6 → 50 points      |
| Chance              | Sum of all dice regardless of the combination         | 1 2 3 4 6 → Sum of all dice|

### Bonus Points

- If the sum of the upper section scores (Ones through Sixes) is 63 or higher, a bonus of 35 points is added.

### Joker Rules
- The player can use a Yahtzee as a Joker in a lower section category if the corresponding upper section category has already been filled.

## Game Elements

### User Interface
- The game features an intuitive user interface with buttons for rolling the dice, selecting dice to reroll, and choosing scoring combinations.

### Scorecard
- A digital scorecard displays the player's score for each category, making it easy to track progress.

### Dice Animation
- Enjoy a visually appealing dice rolling animation, adding excitement to each turn.

## How to Play

1. **Roll the Dice:** Click the "Roll" button to roll the dice.
2. **Reroll (Optional):** Choose which dice to reroll and click "Roll" again, up to two additional times.
3. **Score the Turn:** Select a scoring combination based on the final dice configuration.
4. **Next Player:** Repeat the process for the next player.

## Installation

1. Clone the repository to your local machine.
   ```bash
   git clone https://github.com/your-username/yahtzee-libgdx.git
