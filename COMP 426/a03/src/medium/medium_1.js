import {variance} from "./data/stats_helpers";


/**
 * Gets the sum of an array of numbers.
 * @param array
 * @returns {*}
 * see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array
 * prototype functions. Very useful
 */
export function getSum(array) {
    let sum = 0;
    array.forEach(element => {
        sum = element + sum;
    });
    return sum;
}


/**
 * Calculates the median of an array of numbers.
 * @param {number[]} array
 * @returns {number|*}
 *
 * example:
 * let array = [3,2,5,6,2,7,4,2,7,5];
 * console.log(getMedian(array)); // 4.5
 */
export function getMedian(array) {
    array.sort(function(a, b){return a-b}); 
    if(array.length % 2 == 0){
        let avg = (array[array.length/2] + (array[(array.length/2)-1]))
        return (avg/2)
    }
    else{
        return (array[Math.floor(array.length/2)]) 
    }
}

/**
 * Calculates statistics (see below) on an array of numbers.
 * Look at the stats_helper.js file. It does variance which is used to calculate std deviation.
 * @param {number[]} array
 * @returns {{min: *, median: *, max: *, variance: *, mean: *, length: *, sum: *, standard_deviation: *}}
 *
 * example:
 * getStatistics([3,2,4,5,5,5,2,6,7])
 * {
  length: 9,
  sum: 39,
  mean: 4.333333333333333,
  median: 5,
  min: 2,
  max: 7,
  variance: 2.6666666666666665,
  standard_deviation: 1.632993161855452
 }
 */
export function getStatistics(array) {
    let mean = (getSum(array)/array.length);
    let variance =0;
    array.forEach(num => {
        variance += (num-mean)*(num-mean) 
    })
    let obj = {
        "length" : array.length, 
        "sum" : getSum(array),
        "mean": mean,
        "median": getMedian(array),
        "min": Math.min.apply(Math, array),
        "max": Math.max.apply(Math, array),
        "variance": variance/array.length,
        "standard_deviation": Math.sqrt(variance/array.length)
    }
    return obj;
}

// getSum([5,10]);
// let array = [1,3,12,15,41,54];
// getMedian(array);
getStatistics([3,2,4,5,5,5,2,6,7])

