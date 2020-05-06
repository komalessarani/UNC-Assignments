
let newBoard = [];
export default class Game{
    constructor(size){
        this.size = size;
        this.moveArray = [];
        this.winArray = [];
        this.loseArray = [];
        this.gameState = {
            board: [],
            score: 0,
            won: false,
            over: false
        }
        this.setupNewGame();
    }

    
    setupNewGame() {
        //initialize value of board to 0 and find coords where the board is 0, also initialize newBoard values to 0
        for(var i = 0; i < this.size; i++){
            newBoard[i] = [];
            for(var j = 0; j < this.size; j++){
                this.gameState.board[i] = 0;
                newBoard[i][j] = 0;
            }
        }

        let tile1 = this.addTile();
        let tile2 = this.addTile();
        if(tile1.x == tile2.x && tile1.y == tile2.y){
            tile2 = this.addTile();
        }
        let s = 0;
        for(var i = 0; i < this.size; i++){
            for(var j = 0; j < this.size; j++){
                this.gameState.board[s] = newBoard[i][j];
                s++;
            }
        }
    }

    addTile(){
        let options = [];
        for(let i = 0; i < this.size; i++){
            for(let j = 0; j < this.size; j++){
                if(newBoard[i][j] == 0 || this.gameState.board == 0){
                    options.push({
                        x: i,
                        y: j
                    });
                }
            }
        }
        //randomize tile in the board to either be a 2 or 4 
        //should be 90% chance of a 2 and 10% chance of a 4
        if(options.length > 0){
            let spot1 = options[Math.floor(Math.random() * options.length)];
            let r = Math.random(1);
            newBoard[spot1.x][spot1.y] = r > 0.1 ? 2 : 4;
            return spot1;

        }
    }

    loadGame(gameState){
        this.gameState = gameState;
        for(var i = 0; i < this.size; i++){
            for(var j = 0; j < this.size; j++){
                newBoard[i][j] = this.gameState.board[(i*this.size)+j]
            }
        }
    }

    slide(row){
        let arr = row.filter(val=>val);
        let missing = this.size - arr.length;
        let zeros = Array(missing).fill(0);
        arr = zeros.concat(arr);
        return arr;
    }
    
    combine(row){
        for(let i = this.size-1; i >= 0; i--){
            let a = row[i];
            let b = row[i-1];
            if(a == b){
                row[i] = a+b;
                this.gameState.score += row[i];
                row[i-1] = 0;
            }
        }
        return row;
    }

    operate(row){
        row = this.slide(row)
        row = this.combine(row)
        row = this.slide(row);
        return row;
    }

    compare(matrix1, matrix2){
        for(let i = 0; i < this.size; i++){
            for(let j = 0; j < this.size; j++){
                if(matrix1[i][j] != matrix2[i][j]){
                    return true;
                }
            }
        }
        return false;
    }

    flip(grid){
        for(let i = 0; i < this.size; i++){
            grid[i].reverse();
        }
        return grid;
    }

    rotate(grid){
        let newGrid = [];
        for(let i = 0; i < this.size; i++){
            newGrid[i] = [];
            for(let j = 0; j < this.size; j++){
                newGrid[i][j] = 0;
                newGrid[i][j] = grid[j][i];
            }
        }
        return newGrid;
    }

    isGameOver(){
        for(let i = 0; i < this.size; i++){
            for(let j = 0; j < this.size; j++){
                if(newBoard[i][j] == 0){
                    return false;
                }
                if(i !== 3 && newBoard[i][j] == newBoard[i+1][j]){
                    return false;
                }
                if(j !== 3 && newBoard[i][j] === newBoard[i][j+1]){
                    return false;
                }
            }
        }
        return true;
    }

    gameWon(){
        for(let i = 0; i < this.size; i++){
            for(let j = 0; j < this.size; j++){
                if(newBoard[i][j] == 2048){
                    return true;
                }
            }
        }
        return false;
    }

    move(direction){
        this.gameState.board = newBoard.reduce((acc, val) => acc.concat(val), []);
        let flipped = false;
        let rotated = false;
        let played = true;
        if(direction === "right"){
            //DO NOTHING
        }
        else if(direction === 'left'){
            newBoard = this.flip(newBoard);
            flipped = true;
        }
        else if(direction === 'up'){
            newBoard = this.rotate(newBoard);
            newBoard = this.flip(newBoard);
            rotated = true;
            flipped = true;
        }
        else if(direction === "down"){
            newBoard = this.rotate(newBoard);
            rotated = true;
        }else{
            played = false;
        }
        if(played){
            let past = newBoard.slice();
            for(let i = 0; i < this.size; i++){
                newBoard[i] = this.operate(newBoard[i]); 
            }
            let changed = this.compare(past, newBoard)
            if(flipped){
                newBoard = this.flip(newBoard);
            }
            if(rotated){
                newBoard = this.rotate(newBoard);
                newBoard = this.rotate(newBoard);
                newBoard = this.rotate(newBoard);
            }
            if(changed){
                this.addTile();
            }
                
            this.gameState.board = newBoard.reduce((acc, val) => acc.concat(val), []);
            //callback for move
            for (let func in this.moveArray)
	            this.moveArray[func](this.gameState);

            //set game over 
            this.gameState.over = this.isGameOver();
            //callback for over
            if(this.gameState.over){
                for (let func in this.loseArray)
	                this.loseArray[func](this.gameState);
            }

            //set game won
            this.gameState.won = this.gameWon();
            //callback for won
            if(this.gameState.won){
                for (let func in this.winArray)
	                this.winArray[func](this.gameState);
            }
        }
    }

    toString(){
        let output="";
        for(var i = 0; i < this.size; i++){
            for(var j = 0; j < this.size; j++){
                if(j % newBoard.length === 0){
                    output+="\n"
                }
                output+=(`[${newBoard[i][j]}] `);
            }
        }
        return output;
    }
    
    onMove(callback){
        return this.moveArray.push(callback);
    }

    onWin(callback){
        return this.winArray.push(callback);
    }

    onLose(callback){
        return this.loseArray.push(callback);
    }

    getGameState(){
        return this.gameState;
    }

}
