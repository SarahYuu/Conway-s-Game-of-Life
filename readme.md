The Game of Life is an infinite, two- dimensional grid of square cells, each of which is in one of two possible states: live or dead (or populated and unpopulated, respectively). Every cell interacts with its eight neighbours and transitions as time progresses. 

How to start the game:   
```gradle build```   
```gradle run```

Notes (certain design choices)
1. At the start of the game, the "Block" button is selected by default.
2. After a user clicks the "Clear" button, they need to reselect shapes in order to draw shapes again. 

This supports a manual mode (pause and resume functionality), where the user can single-step through the animation
- Use the key "M" to toggle between manual mode and the animation mode
- Use the key "N" to advance to the next frame
