/**
 * Course: COMP 426
 * Assignment: a04
 * Author: <type your name here>
 *
 * This script uses jQuery to build an HTML page with content taken from the
 * data defined in data.js.
 */



/**
 * Given a hero object (see data.js), this function generates a "card" showing
 *     the hero's name, information, and colors.
 * @param hero  A hero object (see data.js)
 */
export const renderHeroCard = function(hero) {
    // TODO: Generate HTML elements to represent the hero
    // TODO: Return these elements as a string, HTMLElement, or jQuery object
    // Example: return `<div>${hero.name}</div>`;
    return `<div class ="column is-one-quarter">
        <div class = "card" style = "background-color: ${hero.backgroundColor}">
            <div class = "card-image">
                <img class = "is-rounded" src = "${hero.img}" style = "border: 10px solid #ffffff; border-radius: 100%; display: block; margin: 10px auto 20px;">
            </div>
            <br>
            <p class ="title" style="color: ${hero.color}; text-align: center">${hero.name}</p>
            <div class = "card-content" style = "background-color: #ffffff">
                <p class = "subtitle has-text-grey is-italic">"${hero.subtitle}"</p>
                <h5 class ="title-is-5"> <span style = "font-weight:bold">Alter ego: </span>${hero.first} ${hero.last}</h5>
                <h5 class ="title-is-5"> <span style = "font-weight:bold">First Appearance: </span>${hero.firstSeen.toLocaleDateString()}</h5>
                <br>
                <p class ="title-is-6">${hero.description}</p>
                <br>
                <button class="button is-dark is-normal">Edit</button>
            </div>
        </div>
    </div>`;
};


/**
 * Given a hero object, this function generates a <form> which allows the
 *     user to edit the fields of the hero. The form inputs should be
 *     pre-populated with the initial values of the hero.
 * @param hero  The hero object to edit (see data.js)
 */
export const renderHeroEditForm = function(hero) {
    // TODO: Generate HTML elements to represent the hero edit form
    // TODO: Return these elements as a string, HTMLElement, or jQuery object
    // Example: return `<form>${hero.name}</form>`;
    return `<div class ="column is-one-quarter">
    <div class = "card" style = "background-color: ${hero.backgroundColor}">
        <div class = "card-image">
            <img class = "is-rounded" src = "${hero.img}" style = "border: 10px solid #ffffff; border-radius: 100%; display: block; margin: 10px auto 20px;">
        </div>
        <br>
        <div class = "card-content" style = "background-color: #ffffff">
        <form>
        <div class="field">
            <label class="label">Hero Name</label>
            <div class="control">
                <input class="input" type="text" placeholder="Text Input" value="${hero.name}"">
            </div>
        </div>
            <div class="field">
                <label class="label">First Name</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Text input" value ="${hero.first}">
                </div>
            </div>            
            <div class="field">
                <label class="label">Last Name</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Text input" value = "${hero.last}">
                </div>
            </div>
            <div class="field">
                <label class="label">Subtitle</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Text input" value = "${hero.subtitle}">
                </div>
            </div>
            <div class="field">
            <label class="label">First Seen</label>
            <div class="control">
                <input class="input" type="date" placeholder="Text input" value = "${hero.firstSeen.toISOString().substr(0,10)}">
            </div>
        </div>

          <br>
            <div class="field">
                <label class="label">Message</label>
                <div class="control">
                    <textarea class="textarea" placeholder="Textarea">${hero.description}</textarea>
                </div>
            </div>
            <br>
            <button class="button is-danger is-normal">Cancel</button>
            <button type = "submit" class="button is-dark is-normal">Save</button>
        </form>
        </div>
    </div>
</div>`
};



/**
 * Given an array of hero objects, this function converts the data into HTML and
 *     loads it into the DOM.
 * @param heroes  An array of hero objects to load (see data.js)
 */
export const loadHeroesIntoDOM = function(heroes) {
    // Grab a jQuery reference to the root HTML element
    const $root = $('#root');

    // TODO: Generate the heroes using renderHeroCard()

    heroes.forEach(element => {
        $($root).append(renderHeroCard(element))
    });

    // TODO: Append the hero cards to the $root element
    // Pick a hero from the list at random
    const randomHero = heroes[Math.floor(Math.random() * heroes.length)];

    // TODO: Generate the hero edit form using renderHeroEditForm()
    $($root).append(renderHeroEditForm(randomHero));
    // TODO: Append the hero edit form to the $root element
};



/**
 * Use jQuery to execute the loadHeroesIntoDOM function after the page loads
 */
$(function() {
    loadHeroesIntoDOM(heroicData);
});
