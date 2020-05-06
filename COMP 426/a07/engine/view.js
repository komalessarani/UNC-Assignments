import Game from "./game.js";

const $root = $('#root');
let game = new Game(4);
export const setup = document.getElementById("bodyLoad").onload = function() {
    $($root).append(`
    <div class = "wrapper"> <div id = "score"><p>SCORE</p> ${game.gameState.score}</div>
    <br>
    <div class = "header"><button class="button" id="reset">New Game</button>
    <h1>2048</h1>  </div> 
    <br>
    <h1 id = "won" style="color: green">You won!</h1>
    <br> 
    <div id ="grid">
    </div><div id ="lost">You lost with a score of: <br> ${game.gameState.score} </div>
    </div>
    <br><br>
    <div class ="how">
    <p><span style="font-weight: bolder">HOW TO PLAY:</span> Use your arrow keys to move the tiles. When two tiles with the same number touch, they merge into one!
    </p></div>
    </div>`);
    $("#lost").hide();
    $("#won").hide();
    let count = 0;
    game.gameState.board.forEach(element => {
        if(element == 0){
            $($('#grid')).append(`<div id ="box${count}"></div>`)      
            document.getElementById(`box${count}`).style.backgroundColor = "#CDC1B4";      
        }else{
            $($('#grid')).append(`<div id ="box${count}">${element}</div>`)
        }
        count++;
    });
    $('#reset').click(function(){
        game.setupNewGame();
        game.gameState.score = 0;
        updateBoard();
    })
}

function updateBoard(){
    for(let i = 0; i < 16; i++){
        document.getElementById(`box${i}`).innerHTML = game.gameState.board[i];
        if(document.getElementById(`box${i}`).innerHTML == 0){
            $(`#box${i}`).replaceWith(`<div id ="box${i}"></div>`)
            document.getElementById(`box${i}`).style.backgroundColor = "#CDC1B4";
        }else{
            document.getElementById(`box${i}`).style.backgroundColor = "#EEE4DA"
        }   
    }
    document.getElementById(`score`).innerHTML = `<p>SCORE</p> ${game.gameState.score}`;
    $("#won").hide();
}

$(document).keydown(function(e) {
    switch (e.keyCode) {
        case 37:
            game.move('left');
            break;
        case 38:
            game.move('up');
            break;
        case 39:
            game.move('right');
            break;
        case 40:
            game.move('down')
            break;
    }
});

game.onMove(gameState => {
    updateBoard();
});

game.onLose(gameState => {
    document.getElementById(`lost`).innerHTML = `You lost with a score of: <br> ${game.gameState.score}`;
    $("#grid").hide();
    $("#lost").show();
    $(function(){
        $('#reset').click(function(){
            game.setupNewGame();
            game.gameState.score = 0;
            updateBoard();
            $("#grid").show();
            $("#lost").hide();
        })
    })
});



game.onWin(gameState => {
    document.getElementById(`won`).innerHTML = `You won!`;
    $("#won").show();
});

