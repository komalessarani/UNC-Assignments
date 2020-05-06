/**
 * a06:s07
 * Async/await solution to the maze
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
  const doMoves = async function() {
    await token.moveAsync('north');
    await token.moveAsync('east');
    await token.moveAsync('east');
    await token.moveAsync('north');
};
doMoves();


  //  /\ /\ /\ /\ /\
}
