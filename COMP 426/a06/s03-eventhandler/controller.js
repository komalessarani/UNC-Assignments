/**
 * a06:s03
 * Event handler solution to the maze
 */


/**
 * Runs when the page loads
 */
document.body.onload = async function () {
  // Create a basic maze
  const maze = new Maze();

  // Add a token to the maze
  const token = new Token(maze);

  // Attach the maze to the dom
  document.getElementById('root').appendChild(maze.dom);

  // TODO: Write code to solve the maze here
  //  \/ \/ \/ \/ \/
  const moves = ['north', 'east', 'east', 'north'];
  token.moveAsync(moves.shift());
  token.onmoveend = function() {
      if (moves.length > 0) {
          token.moveAsync(moves.shift());
      }
  };


  //  /\ /\ /\ /\ /\
}
