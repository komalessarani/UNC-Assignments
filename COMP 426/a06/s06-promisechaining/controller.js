/**
 * a06:s06
 * Promise chaining solution to the maze
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
  token.moveAsync('north').then(function() {
    return token.moveAsync('east');
}).then(function() {
    return token.moveAsync('east');
}).then(function() {
    return token.moveAsync('north');
});


  //  /\ /\ /\ /\ /\
}
