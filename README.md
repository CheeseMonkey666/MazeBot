# MazeBot
Simple Java application that solves mazes with 1 pixel wide corridors.

A blue pixel defines the end point of a maze, whereas a red pixel defines the start. If you have multiple start points then only the one closest to the bottom right will be used. Walls must be black and corridors must be white and be one pixel wide for MazeBot to function correctly.

Mazes have to be saved in BMP format (for now) and have a maximum resolution of 300x450.

To use, simply open the application, click on "Open Map", select a valid file from your computer and click on the "Walk" button. Recursive mode is a planned feature but has not yet been implemented.
