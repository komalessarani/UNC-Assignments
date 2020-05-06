/**
 * a06:s02
 * Asynchronous solution to the maze
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
  token.moveAsync('north');
  token.moveAsync('east');
  token.moveAsync('east');
  token.moveAsync('north');


  //  /\ /\ /\ /\ /\
}
