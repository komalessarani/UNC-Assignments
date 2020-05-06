let tweetsArr = [];

//has event listeners on the buttons and renders each tweet
export const renderSite = function(){
  const $root = $("#root");
  renderTweets();  
  $($root).on("click", '#like', likeTweet);
  $($root).on("click", '#add', openDialog);
  $($root).on("click", '#edit', openEdit);
  $($root).on("click", '#trash', deleteTweet);
  $($root).on("click", '#comment', openReply);
  $($root).on("click", '#retweet', openRetweet)
}

//opens the create a tweet textarea
function openDialog(){
  document.getElementById("show").style.display ="block";
  document.getElementById("send").style.display ="block";
  $("#root").on("click", '#send', createTweet);
}

//creates new tweet
export async function createTweet(){
  const result = await axios({
    method: 'post',
    url: 'https://comp426fa19.cs.unc.edu/a09/tweets',
    data: {
        "type": "tweet",
        "body": document.getElementById("show").value,
      },
    withCredentials: true,
  });
  $('#tweets').replaceWith(renderTweets());
  document.getElementById("show").style.display ="none";
  document.getElementById("show").value = '';
  document.getElementById("send").style.display ="none";
}

//opens the reply textarea
function openReply(){
  document.getElementById(`commentShow ${event.target.id}`).style.display ="block";
  document.getElementById(`saveComment${event.target.id}`).style.display ="block";
  $("#root").on("click", `#saveComment${event.target.id}`, replyTweet);
}

//replies to a tweet
export async function replyTweet() {
  const result = await axios({
      method: 'post',
      url: "https://comp426fa19.cs.unc.edu/a09/tweets",
      withCredentials: true,
      data: {
        "type": "reply",
        "parent": event.target.parentElement.id,
        "body": document.getElementById(`commentShow ${event.target.parentElement.id}`).value
      },
    });
    $('#tweets').replaceWith(renderTweets());
  }

//opens the retweet textarea
function openRetweet(){
  document.getElementById(`retweetShow ${event.target.parentElement.id}`).style.display ="block";
  document.getElementById(`saveRetweet${event.target.parentElement.id}`).style.display ="block";
  $("#root").on("click", `#saveRetweet${event.target.parentElement.id}`, retweetTweet);
}

//retweets a tweet
export async function retweetTweet() {
  const post = await axios({
      method: 'get',
      url: 'https://comp426fa19.cs.unc.edu/a09/tweets/' + event.target.parentElement.id,
      withCredentials: true,
    })
  let id = (JSON.parse(event.currentTarget.response).id)
  let string = document.getElementById(`retweetShow ${id}`).value; 

  const result = await axios({
      method: 'post',
      url: 'https://comp426fa19.cs.unc.edu/a09/tweets',
      withCredentials: true,
      data: {
        "type": "retweet",
        "parent": id,
        "body": `${string}<br>`,
      },
    });
  $('#tweets').replaceWith(renderTweets());
}

//opens the editing a tweet textarea
function openEdit(){
  document.getElementById("editShow").style.display ="block";
  document.getElementById("saveEdit").style.display ="block";
  $("#root").on("click", '#saveEdit', editTweet);
}

//edits tweet 
export async function editTweet(){
  const result = await axios({
      method: 'put',
      url: "https://comp426fa19.cs.unc.edu/a09/tweets/" + event.target.parentElement.id,
      withCredentials: true,
      data: {
         "body": document.getElementById("editShow").value,
      },
  });
  $('#tweets').replaceWith(renderTweets());
}

//deletes tweet
export async function deleteTweet(){
  let conf = confirm("Are you sure you want to delete this tweet?")
  if(conf === true){
    let url = "https://comp426fa19.cs.unc.edu/a09/tweets/" + event.target.parentElement.id;
    const result = await axios({
        method: 'delete',
        url: url,
        withCredentials: true,
      }); 
    $('#tweets').replaceWith(renderTweets());
  }else{
    return;
  }
}

//likes tweet
export async function likeTweet() {
  let isLiked;
  tweetsArr.forEach(val=>{
    if(val.id == event.target.id){
      isLiked = val.isLiked;
    }
  })
  if (isLiked===false) {
      const result = await axios({
          method: 'put',
          url: "https://comp426fa19.cs.unc.edu/a09/tweets/" + event.target.id + "/like",
          withCredentials: true,
        });
    } else{
      const result2 = await axios({
          method: 'put',
          url: "https://comp426fa19.cs.unc.edu/a09/tweets/" + event.target.id + "/unlike",
          withCredentials: true,
        }); 
  }
  $('#tweets').replaceWith(renderTweets());
} 

//renders each indvidual tweet depending on whether it is mine or if it is a tweet or retweet
export async function renderTweets() {
  const $root = $("#root");
  const result = await axios({
    method: 'get',
    url: 'https://comp426fa19.cs.unc.edu/a09/tweets',
    withCredentials: true,
  })

  let tweets = `<div id = "tweets">`;
 
  for (let i = 0; i < 50; i++) {
    if (result.data[i]["isMine"] == true && result.data[i].type ==="tweet") {
    tweets += myTweets(result.data[i])//Insert code where you set up your own html for a post that is yours ie. you have to have delete and edit buttons
    } else if(!(result.data[i].isMine) && result.data[i].type === "tweet"){
    tweets += otherTweets(result.data[i])//Insert code where you set up your own html for a post that is not yours   ie. you shouldn’t have delete or edit buttons
    }else if(!(result.data[i].isMine) && result.data[i].type === "retweet"){
      tweets+= renderRetweets(result.data[i]);
    }else if(result.data[i].isMine && result.data[i].type === "retweet"){
      tweets += renderMyRetweets(result.data[i]);
    }
  }
  tweets += `</div>`;
  $root.append(tweets);
  tweetsArr = result.data;
}

//my tweets that are NOT retweets
function myTweets(tweet){
  let at = tweet.author;

  at = (at.toLowerCase()).replace(/\s/g, '');
  at = at.replace(/\./g, '');
    
  let retweetNum ='';
  let replyNum ='';
  let likeNum = '';
    
    if(tweet.retweetCount > 0){
      retweetNum = tweet.retweetCount;
    }
    if(tweet.likeCount > 0){
      likeNum = tweet.likeCount;
    }
    if(tweet.replyCount > 0){
      replyNum = tweet.replyCount;
    }

    return `<div class = "wrapper" id="parent">
    <div class = "card" style = "background-color: #15202B;">
        <br>
        <div class="card-content" style = "color: #FFFFFF;">
    <div class="media">
      <div class="media-left">
        <figure class="image is-48x48">
        <i class="fa fa-user fa-3x" aria-hidden="true" style="color: #1DA0F0;"></i>
        </figure>
      </div>
      <div class="media-content">
        <p class="title is-4" style = "color: #FFFFFF;">${tweet.author}</p>
        <p class="subtitle is-6">@${at}</p>
      </div>
    </div>

    <div class="content">
      ${tweet.body}     <br>
    </div>
    <div class ="button-content">
    <button class = "button is-primary" id ="comment" style="background-color: #15202B">
        <span class="icon">
          <i class="fa fa-comment-o" id="${tweet.id}"></i>
        </span>
        <p>${replyNum}</p>
      </button>
      <button class = "button is-primary" id ="retweet" style="background-color: #15202B">
        <span class="icon" id="${tweet.id}">
          <i class="fa fa-retweet""></i>
        </span>
        <p id="retweetnumber ${tweet.id}">${retweetNum}</p>
      </button>
      <button class = "button is-primary" id ="like" style="background-color: #15202B; color: ${tweet.isLiked ? "red" : "#63716C"}" disabled> 
        <span class="icon">
          <i class="fa fa-heart-o" id="${tweet.id}"></i>
        </span>
        <p id="${tweet.id}" class ="likenum">${likeNum}</p>
      </button>
    </div>
    <br>
    <div class = "button-content more" style="display: inline-block">
    <button class = "button is-primary" id ="trash" style="background-color: #15202B; color: white">
        <span class="icon" id ="${tweet.id}">
          <i class="fa fa-trash fa-lg"></i>
        </span>
      </button>
      </div>
      <div style="display: inline-block">
      <button class = "button is-primary" id ="edit" style="background-color: #15202B; color: white">
        <span class="icon">
          <i class="fa fa-pencil-square-o fa-lg"></i>
        </span>
      </button>
      </div>

      <div id="${tweet.id}">
      <textarea id ="editShow" style ="width: 100%; display: none;">${tweet.body}</textarea>
      <button id ="saveEdit" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
      <textarea id ="commentShow ${tweet.id}" style ="width: 100%; display: none;" placeholder="Type your reply here."></textarea>
      <button id ="saveComment${tweet.id}" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
      <textarea id ="retweetShow ${tweet.id}" style ="width: 100%; display: none;" placeholder="Type your retweet here."></textarea>
      <button id ="saveRetweet${tweet.id}" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
      </div>  
  </div>
    </div>
</div>`;
}

//my retweets
function renderMyRetweets(tweet){
  if(tweet.parent == undefined){
    return;
  }

  let retweetNum ='';
  let replyNum ='';
  let likeNum = '';
    
    if(tweet.retweetCount > 0){
      retweetNum = tweet.retweetCount;
    }
    if(tweet.likeCount > 0){
      likeNum = tweet.likeCount;
    }
    if(tweet.replyCount > 0){
      replyNum = tweet.replyCount;
    }

  let rtAt = tweet.author;
  rtAt = (rtAt.toLowerCase()).replace(/\s/g, '');
  rtAt = rtAt.replace(/\./g, '');

  let at = tweet.parent.author;
  at = (at.toLowerCase()).replace(/\s/g, '');
  at = at.replace(/\./g, '');

  return `<div class = "wrapper rt" id="parent rt">
            <div class = "card" style = "background-color: #15202B;">
              <br>
              <div class="card-content" style = "color: #FFFFFF;">
                <div class="media">
                  <div class="media-left">
                    <figure class="image is-48x48">
                    <i class="fa fa-user fa-3x" aria-hidden="true" style="color: #1DA0F0;"></i>
                    </figure>
                  </div>
                  <div class="media-content">
                    <p class="title is-4" style = "color: #FFFFFF;">${tweet.author}</p>
                    <p class="subtitle is-6">@${rtAt}</p>
                  </div>
                </div>
              <div class="content">
                ${tweet.body}     <br>
                <div class = "card inner" style = "background-color: #FFFFFF;">
                <br>
                <div class="card-content inner" style = "color: #15202B;">
                  <div class="media inner">
                    <div class="media-left inner">
                      <figure class="image is-48x48">
                      <i class="fa fa-user fa-3x" aria-hidden="true" style="color: #1DA0F0;"></i>
                      </figure>
                    </div>
                  <div class="media-content inner">
                    <p class="title is-4" style = "color: #15202B;">${tweet.parent.author}</p>
                    <p class="subtitle is-6">@${at}</p>
                  </div>
                </div>

                <div class="content inner">
                  ${tweet.parent.body}     <br>
                </div>
                </div>
                </div>
              </div>
              <div class ="button-content">
                  <button class = "button is-primary" id ="comment" style="background-color: #15202B">
                    <span class="icon">
                      <i class="fa fa-comment-o" id="${tweet.id}"></i>
                    </span>
                    <p>${replyNum}</p>
                  </button>
                  <button class = "button is-primary" id ="retweet" style="background-color: #15202B">
                    <span class="icon" id="${tweet.id}">
                      <i class="fa fa-retweet""></i>
                    </span>
                    <p id="retweetnumber ${tweet.id}">${retweetNum}</p>
                  </button>
                  <button class = "button is-primary" disabled id ="like" style="background-color: #15202B; color: ${tweet.isLiked ? "red" : "#63716C"}"> 
                    <span class="icon">
                      <i class="fa fa-heart-o" id="${tweet.id}"></i>
                    </span>
                    <p id="${tweet.id}" class ="likenum">${likeNum}</p>
                  </button>
                </div>
                <br>
                <div class = "button-content more" style="display: inline-block">
                <button class = "button is-primary" id ="trash" style="background-color: #15202B; color: white">
                    <span class="icon" id ="${tweet.id}">
                      <i class="fa fa-trash fa-lg"></i>
                    </span>
                  </button>
                  </div>
                  <div style="display: inline-block">
                  <button class = "button is-primary" id ="edit" style="background-color: #15202B; color: white">
                    <span class="icon">
                      <i class="fa fa-pencil-square-o fa-lg"></i>
                    </span>
                  </button>
                  </div>
            
                  <div id="${tweet.id}">
                  <textarea id ="editShow" style ="width: 100%; display: none;">${tweet.body}</textarea>
                  <button id ="saveEdit" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
                  <textarea id ="commentShow ${tweet.id}" style ="width: 100%; display: none;" placeholder="Type your reply here."></textarea>
                  <button id ="saveComment${tweet.id}" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
                  <textarea id ="retweetShow ${tweet.id}" style ="width: 100%; display: none;" placeholder="Type your retweet here."></textarea>
                  <button id ="saveRetweet${tweet.id}" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
                  </div>  
            </div>
          </div>
          </div>`;
}


//other people's retweets
function renderRetweets(tweet){
  if(tweet.parent == undefined){
    return;
  }

  let rtAt = tweet.author;
  rtAt = (rtAt.toLowerCase()).replace(/\s/g, '');
  rtAt = rtAt.replace(/\./g, '');

  let at = tweet.parent.author;
  at = (at.toLowerCase()).replace(/\s/g, '');
  at = at.replace(/\./g, '');

  let retweetNum ='';
  let replyNum ='';
  let likeNum = '';
    
    if(tweet.retweetCount > 0){
      retweetNum = tweet.retweetCount;
    }
    if(tweet.likeCount > 0){
      likeNum = tweet.likeCount;
    }
    if(tweet.replyCount > 0){
      replyNum = tweet.replyCount;
    }

  return `<div class = "wrapper rt" id="parent rt">
            <div class = "card" style = "background-color: #15202B;">
              <br>
              <div class="card-content" style = "color: #FFFFFF;">
                <div class="media">
                  <div class="media-left">
                    <figure class="image is-48x48">
                    <i class="fa fa-user fa-3x" aria-hidden="true" style="color: #1DA0F0;"></i>
                    </figure>
                  </div>
                  <div class="media-content">
                    <p class="title is-4" style = "color: #FFFFFF;">${tweet.author}</p>
                    <p class="subtitle is-6">@${rtAt}</p>
                  </div>
                </div>
              <div class="content">
                ${tweet.body}     <br>
                <div class = "card inner" style = "background-color: #FFFFFF;">
                <br>
                <div class="card-content inner" style = "color: #15202B;">
                  <div class="media inner">
                    <div class="media-left inner">
                      <figure class="image is-48x48">
                      <i class="fa fa-user fa-3x" aria-hidden="true" style="color: #1DA0F0;"></i>
                      </figure>
                    </div>
                  <div class="media-content inner">
                    <p class="title is-4" style = "color: #15202B;">${tweet.parent.author}</p>
                    <p class="subtitle is-6">@${at}</p>
                  </div>
                </div>

                <div class="content inner">
                  ${tweet.parent.body}     <br>
                </div>
                </div>
                </div>
              </div>
              <div class ="button-content">
                  <button class = "button is-primary" id ="comment" style="background-color: #15202B">
                    <span class="icon">
                      <i class="fa fa-comment-o" id="${tweet.id}"></i>
                    </span>
                    <p>${replyNum}</p>
                  </button>
                  <button class = "button is-primary" id ="retweet" style="background-color: #15202B">
                    <span class="icon" id="${tweet.id}">
                      <i class="fa fa-retweet""></i>
                    </span>
                    <p id="retweetnumber ${tweet.id}">${retweetNum}</p>
                  </button>
                  <button class = "button is-primary" id ="like" style="background-color: #15202B; color: ${tweet.isLiked ? "red" : "#63716C"}"> 
                    <span class="icon">
                      <i class="fa fa-heart-o" id="${tweet.id}"></i>
                    </span>
                    <p id="${tweet.id}" class ="likenum">${likeNum}</p>
                  </button>
                </div>
              <div id="${tweet.id}">
                <textarea id ="commentShow ${tweet.id}" style ="width: 100%; display: none;" placeholder="Type your reply here."></textarea>
                <button id ="saveComment${tweet.id}" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
                <textarea id ="retweetShow ${tweet.id}" style ="width: 100%; display: none;" placeholder="Type your retweet here."></textarea>
                <button id ="saveRetweet${tweet.id}" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
              </div> 
            </div>
          </div>
          </div>`;
}

//other people's new tweets
function otherTweets(tweet){
  let at = tweet.author;

  at = (at.toLowerCase()).replace(/\s/g, '');
  at = at.replace(/\./g, '');
  let retweetNum ='';
  let replyNum ='';
  let likeNum = '';
    
    if(tweet.retweetCount > 0){
      retweetNum = tweet.retweetCount;
    }
    if(tweet.likeCount > 0){
      likeNum = tweet.likeCount;
    }
    if(tweet.replyCount > 0){
      replyNum = tweet.replyCount;
    }

    return `<div class = "wrapper" id="parent">
              <div class = "card" style = "background-color: #15202B;">
                <br>
                <div class="card-content" style = "color: #FFFFFF;">
                  <div class="media">
                    <div class="media-left">
                      <figure class="image is-48x48">
                      <i class="fa fa-user fa-3x" aria-hidden="true" style="color: #1DA0F0;"></i>
                      </figure>
                    </div>
                  <div class="media-content">
                    <p class="title is-4" style = "color: #FFFFFF;">${tweet.author}</p>
                    <p class="subtitle is-6">@${at}</p>
                  </div>
                </div>

                <div class="content">
                  ${tweet.body}     <br>
                </div>
                <div class ="button-content">
                  <button class = "button is-primary" id ="comment" style="background-color: #15202B">
                    <span class="icon">
                      <i class="fa fa-comment-o" id="${tweet.id}"></i>
                    </span>
                    <p>${replyNum}</p>
                  </button>
                  <button class = "button is-primary" id ="retweet" style="background-color: #15202B">
                    <span class="icon" id="${tweet.id}">
                      <i class="fa fa-retweet""></i>
                    </span>
                    <p id="retweetnumber ${tweet.id}">${retweetNum}</p>
                  </button>
                  <button class = "button is-primary" id ="like" style="background-color: #15202B; color: ${tweet.isLiked ? "red" : "#63716C"}"> 
                    <span class="icon">
                      <i class="fa fa-heart-o" id="${tweet.id}"></i>
                    </span>
                    <p id="${tweet.id}" class ="likenum">${likeNum}</p>
                  </button>
                </div>
              <div id="${tweet.id}">
                <textarea id ="commentShow ${tweet.id}" style ="width: 100%; display: none;" placeholder="Type your reply here."></textarea>
                <button id ="saveComment${tweet.id}" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
                <textarea id ="retweetShow ${tweet.id}" style ="width: 100%; display: none;" placeholder="Type your retweet here."></textarea>
                <button id ="saveRetweet${tweet.id}" style ="width: 100%; display: none; background-color: #1DA0F0; color: white" class ="button is-primary">Save</button>
              </div> 
            </div>
        </div>
</div>`;
};

//when ready, the whole site will be rendered
$(function render() {
  renderSite();
});
