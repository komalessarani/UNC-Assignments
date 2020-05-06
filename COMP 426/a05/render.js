/**
 * Course: COMP 426
 * Assignment: a05
 * Author: Komal Essarani
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
    // TODO: Copy your code from a04 to render the hero card
    return `<div class ="column is-one-quarter ${hero.id}">
    <div class = "card" style = "background-color: ${hero.backgroundColor}">
        <div class = "card-image">
            <img class = "is-rounded" src = "${hero.img}" alt = "HeroPicture" style = "border: 10px solid #ffffff; border-radius: 100%; display: block; margin: 10px auto 20px;">
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
            <button class="button is-dark is-normal" id="${hero.id}">Edit</button>
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
    // TODO: Copy your code from a04 to render the hero edit form
    return `<div class ="column is-one-quarter ${hero.id}">
    <div class = "card" style = "background-color: ${hero.backgroundColor}">
        <div class = "card-image">
            <img class = "is-rounded" src = "${hero.img}" alt = "HeroPicture" style = "border: 10px solid #ffffff; border-radius: 100%; display: block; margin: 10px auto 20px;">
        </div>
        <br>
        <div class = "card-content" style = "background-color: #ffffff">
        <form>
        <div class="field">
            <label class="label">Hero Name</label>
            <div class="control">
                <input class="input" type="text" placeholder="Text Input" value="${hero.name}" id ="name">
            </div>
        </div>
            <div class="field">
                <label class="label">First Name</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Text input" value ="${hero.first}" id="first">
                </div>
            </div>            
            <div class="field">
                <label class="label">Last Name</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Text input" value = "${hero.last}" id="last">
                </div>
            </div>
            <div class="field">
                <label class="label">Subtitle</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Text input" value = "${hero.subtitle}" id="subtitle">
                </div>
            </div>
            <div class="field">
            <label class="label">First Seen</label>
            <div class="control">
                <input class="input date" type="date" id="firstSeen" value = "${hero.firstSeen.toISOString().substring(0,10)}">
            </div>
        </div>

          <br>
            <div class="field">
                <label class="label">Message</label>
                <div class="control">
                    <textarea class="textarea" placeholder="Textarea" id ="description">${hero.description}</textarea>
                </div>
            </div>
            <br>
            <button class="button is-danger is-normal" id ="cancel.${hero.id}" type ="button">Cancel</button>
            <button type = "submit" class="button is-dark is-normal submit" id ="${hero.id}">Save</button>
        </form>
        </div>
    </div>
</div>`
};



/**
 * Handles the JavaScript event representing a user clicking on the "edit"
 *     button for a particular hero.
 * @param event  The JavaScript event that is being handled
 */
export const handleEditButtonPress = function(event) {
    // TODO: Render the hero edit form for the clicked hero and replace the
    //       hero's card in the DOM with their edit form instead
    event.preventDefault();
    heroicData.forEach(hero =>{
        if(hero.id == event.target.id){
            $(`.column.is-one-quarter.${hero.id}`).replaceWith(renderHeroEditForm(hero))
        }
    })
    $('.button.is-danger.is-normal').on("click", handleCancelButtonPress)
    $('.button.is-dark.is-normal.submit').on("click", handleEditFormSubmit)

};



/**
 * Handles the JavaScript event representing a user clicking on the "cancel"
 *     button for a particular hero.
 * @param event  The JavaScript event that is being handled
 */
export const handleCancelButtonPress = function(event) {
    // TODO: Render the hero card for the clicked hero and replace the
    //       hero's edit form in the DOM with their card instead
    event.preventDefault();
    var txt = event.target.id;
    var numb = txt.match(/\d/g);
    numb = numb.join('')
    heroicData.forEach(hero =>{
        if(hero.id == numb){
            $(`.column.is-one-quarter.${hero.id}`).replaceWith(renderHeroCard(hero))
        }
    })
};



/**
 * Handles the JavaScript event representing a user clicking on the "cancel"
 *     button for a particular hero.
 * @param event  The JavaScript event that is being handled
 */
export const handleEditFormSubmit = function(event) {
    // TODO: Render the hero card using the updated field values from the
    //       submitted form and replace the hero's edit form in the DOM with
    //       their updated card instead
    let dat = new Date($(".input.date").val())
    dat.setDate(dat.getDate()+1)
    heroicData.forEach(hero =>{
        if(hero.id == event.target.id){
            hero.name = document.getElementById("name").value
            hero.first = document.getElementById("first").value
            hero.last = document.getElementById("last").value
            hero.subtitle = document.getElementById("subtitle").value
            hero.description = document.getElementById("description").value
            hero.firstSeen = new Date((new Date(document.getElementById("firstSeen").value)).getTime() + Math.abs((new Date(document.getElementById("firstSeen").value)).getTimezoneOffset()*60000));
            $(`.column.is-one-quarter.${hero.id}`).replaceWith(renderHeroCard(hero))
        }
    })
};
 


/**
 * Given an array of hero objects, this function converts the data into HTML,
 *     loads it into the DOM, and adds event handlers.
 * @param  heroes  An array of hero objects to load (see data.js)
 */
export const loadHeroesIntoDOM = function(heroes) {
    // Grab a jQuery reference to the root HTML element
    const $root = $('#root');

    // TODO: Generate the heroes using renderHeroCard()
    //       NOTE: Copy your code from a04 for this part
    
    // TODO: Append the hero cards to the $root element
    //       NOTE: Copy your code from a04 for this part

    heroes.forEach(element => {
        $($root).append(renderHeroCard(element))
    });

    // TODO: Use jQuery to add handleEditButtonPress() as an event handler for
    //       clicking the edit button
    $("#root").on("click", '.button.is-dark.is-normal', handleEditButtonPress);

    // TODO: Use jQuery to add handleEditFormSubmit() as an event handler for
    //       submitting the form
    $('.button.is-dark.is-normal.submit').on("click", handleEditFormSubmit)

    // TODO: Use jQuery to add handleCancelButtonPress() as an event handler for
    //       clicking the cancel button
    $('.button.is-danger.is-normal').on("click", handleCancelButtonPress)
};



/**
 * Use jQuery to execute the loadHeroesIntoDOM function after the page loads
 */
$(function() {
    loadHeroesIntoDOM(heroicData);
});
