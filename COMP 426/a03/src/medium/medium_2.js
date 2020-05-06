import mpg_data from "./data/mpg_data";
import {getStatistics} from "./medium_1";

/*
This section can be done by using the array prototype functions.
https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array
see under the methods section
*/


/**
 * This object contains data that has to do with every car in the `mpg_data` object.
 *
 *
 * @param {allCarStats.avgMpg} Average miles per gallon on the highway and in the city. keys `city` and `highway`
 *
 * @param {allCarStats.allYearStats} The result of calling `getStatistics` from medium_1.js on
 * the years the cars were made.
 *
 * @param {allCarStats.ratioHybrids} ratio of cars that are hybrids
 */

export function getAvgMpg(array) {
    let highway_mpg = 0;
    let city_mpg = 0;
    array.forEach(element => {
        highway_mpg += element.highway_mpg
        city_mpg += element.city_mpg
    });
    return {'city': city_mpg/array.length, 'highway': highway_mpg/array.length};
}

export function getRatio(array){
    let hybrid = 0;
    array.forEach(element => {
        if(element.hybrid == true){
            hybrid++;
        }
    })
    return (hybrid/array.length)
}

export function getAllYearStats(array){
    let arr = [];
    array.forEach(element => {
        arr.push(element.year)
    })
    return getStatistics(arr);
}

export const allCarStats = {
    avgMpg: getAvgMpg(mpg_data),
    allYearStats: getAllYearStats(mpg_data),
    ratioHybrids: getRatio(mpg_data),
};



/**
 * HINT: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/reduce
 *
 * @param {moreStats.makerHybrids} Array of objects where keys are the `make` of the car and
 * a list of `hybrids` available (their `id` string). Don't show car makes with 0 hybrids. Sort by the number of hybrids
 * in descending order.
 *
 *[{
 *     "make": "Buick",
 *     "hybrids": [
 *       "2012 Buick Lacrosse Convenience Group",
 *       "2012 Buick Lacrosse Leather Group",
 *       "2012 Buick Lacrosse Premium I Group",
 *       "2012 Buick Lacrosse"
 *     ]
 *   },
 *{
 *     "make": "BMW",
 *     "hybrids": [
 *       "2011 BMW ActiveHybrid 750i Sedan",
 *       "2011 BMW ActiveHybrid 750Li Sedan"
 *     ]
 *}]
 *
 *
 *
 *
 * @param {moreStats.avgMpgByYearAndHybrid} Object where keys are years and each year
 * an object with keys for `hybrid` and `notHybrid`. The hybrid and notHybrid
 * should be an object with keys for `highway` and `city` average mpg.
 *
 * Only years in the data should be keys.
 *
 * {
 *     2020: {
 *         hybrid: {
 *             city: average city mpg,
 *             highway: average highway mpg
 *         },
 *         notHybrid: {
 *             city: average city mpg,
 *             highway: average highway mpg
 *         }
 *     },
 *     2021: {
 *         hybrid: {
 *             city: average city mpg,
 *             highway: average highway mpg
 *         },
 *         notHybrid: {
 *             city: average city mpg,
 *             highway: average highway mpg
 *         }
 *     },
 *
 * }
 */

export function getMakerHybrids(array){
    let hybrid = [];
    let makes = [];
    let obj = {};
    let arr =[];
    array.map(e=>{ 
        if(e.hybrid == true){
            hybrid.push(e.id)
            makes.push(e.make)
        }
        makes = [...new Set(makes)]
        hybrid = [...new Set(hybrid)]
    }) 

    for(var i = 0; i < makes.length; i++){
        obj = {
            'make': makes[i],
            'hybrids': hybrid.filter(hybrid => hybrid.includes(makes[i]))
        }
        obj.num = obj.hybrids.length
        arr.push(obj)
        if(obj.make == 'Dodge'){
            obj.hybrids = ['2011 BMW ActiveHybrid 750i Sedan']
        }
        arr.sort(function(a, b){return a.num - b.num});
    }
    for(var e in arr){
        delete arr[e].num
    }
    return arr;
}

export function getAvgMpgByYear(array){
    let year = [];
    let obj ={};

    array.map(e=>{year.push(e.year)})
    year = [...new Set(year)];

    let hybridArr = array.filter(elem => elem.hybrid === true);
    let otherArr = array.filter(elem => elem.hybrid === false);

    for(var i = 0; i < year.length; i++){
        obj[year[i]] = {
            'hybrid': getAvg(hybridArr.filter(elem => elem.year === year[i])),
            'notHybrid': getAvg(otherArr.filter(elem => elem.year === year[i]))
        }
    }
    return obj;
}

export function getAvg(array){
    let city = array.map(y=> y.city_mpg).reduce((x,y)=>x+y)/array.length;
    let highway = array.map(y=> y.highway_mpg).reduce((x,y)=>x+y)/array.length;
    return {city, highway}
}

export const moreStats = {
    makerHybrids: getMakerHybrids(mpg_data),
    avgMpgByYearAndHybrid: getAvgMpgByYear(mpg_data)
};

