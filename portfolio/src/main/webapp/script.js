// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random quote to the page. Source: https://www.inc.com/bill-murphy-jr/366-top-inspirational-quotes-motivational-quotes-for-every-single-day-in-2020.html
 */
function addRandomQuote() {
  const quotes =
      ['"Normality is a paved road: It\'s comfortable to walk, but no flowers grow on it."\n- Vincent Van Gogh', '"Positive thinking will let you do everything better than negative thinking will."\n- Zig Ziglar', 
      '"I look for the day... when the only criterion of excellence or position shall be the ability and character of the individual; and this time will come."\n- Susan B. Anthony',
      '"Use your fear... it can take you to the place where you store your courage."\n- Amelia Earhart', '"If it wasn\'t hard, everyone would do it. It\'s the hard that makes it great."\n- Tom Hanks',
      '"One doesn\'t discover new lands without consenting to lose sight, for a very long time, of the shore."\n- Andre Gide',
      '"The fastest way to change yourself is to hang out with people who are already the way you want to be."\n- Reid Hoffman', '"To see we must forget the name of the thing we are looking at."\n- Claude Monet',
      '"Practice what you know, and it will help to make clear what now you do not know."\n- Rembrandt',
      '"There can be no greater gift than that of giving one\'s time and energy to help others without expecting anything in return."\n- Nelson Mandela',
      '"When the whole world is silent, even one voice becomes powerful."\n- Malala Yousafzai', '"Don\'t worry about failure; you only have to be right once."- Drew Houston',
      '"It\'s not in the dreaming, it\'s in the doing."\n- Mark Cuban'];

  // Pick a random quote.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  // Add it to the page.
  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;
}

/**
 * Tells server to delete comment
 */
function deleteComment(comment) {
    const fields = new URLSearchParams();
    fields.append('id', comment.id);
    fetch('/delete-comment', {method: 'POST', body: fields}).then(response => getComments());
}

/**
 * Fetches data and handles response for contact page comments
 */
function getComments() {
    // is the fetch string hardcoded? better way to get query string?
  fetch('/comments?comment-limit=' + getValue("comment-limit") + '&comment-filter=' + 
    getValue("comment-filter") + '&comment-sort=' + getValue("comment-sort"))
        .then(response => response.json()).then(comments => {
            const commentsElement = document.getElementById('comments-list');
            updateComments(comments, commentsElement);
        });
}

/**
 * Get attribute name value
 */
function getValue(name) {
    return document.getElementById(name).value;
}

/**
 * Replace old comments with new comments
 */
function updateComments(comments, element) {
    // Clear out old comments
    element.innerHTML = '';

    // Build list of comments
    comments.forEach(comment => {
        element.appendChild(createListElement(comment));
    });
}

/** Creates an <li> element containing text. */
function createListElement(comment) {
    const liElement = document.createElement('li');
    liElement.setAttribute("class", "comment");
    var rounded_score = comment.sentiment_score.toFixed(2);

    // const titleElement = document.createElement('span');
    liElement.innerHTML = comment.content + '<br><br>Posted by ' + 
        comment.user_name + ' (' + comment.user_location + ') at ' + comment.formatted_time +
        '<br>Sentiment score: ' + rounded_score;

    setSentimentColor(liElement, comment.sentiment_score);

    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.classList.add("delete-comment");
    deleteButtonElement.innerHTML = "X";
    deleteButtonElement.addEventListener('click', () => {
        deleteComment(comment);

        // Remove the task from the DOM.
        liElement.remove();
    });

    // liElement.appendChild(titleElement);
    liElement.appendChild(deleteButtonElement);
    return liElement;
}

/** Set background color based on sentiment score */
function setSentimentColor(liElement, score) {
    switch(true) {
        case (score < -0.35):
            // Negative - red
            // Set lightness to the range of 50% - 85%, as score will range from -1 to -0.3 => -50 to -15.
            liElement.style.backgroundColor = "hsl(0, 85%, " + (100 + Math.round(score * 50)) + "%)";
            break;
        case (score < 0.35):
            // Neutral - blue
            liElement.style.backgroundColor = "hsl(230, 85%, 85%)";
            break;
        case (score <= 1):
            // Positive - green
            // Set lightness to the range of 50% - 85%, as score will range from 0.3 to 1 => 50 to 15.
            liElement.style.backgroundColor = "hsl(120, 85%, " + (100 - Math.round(score * 50)) + "%)";
            break;
        default:
            break;
    }
}


// Slideshow functions inspired by https://www.w3schools.com/howto/howto_js_slideshow.asp

/**
 * Next/previous controls
 */
function plusSlides(n) {
    showSlides(slideIndex += n);
}

/**
 * Thumbnail image controls
 */
function currentSlide(n) {
    showSlides(slideIndex = n);
}

/**
 * Slideshow helper
 */
function showSlides(n) {
    var i;
    var slides = document.getElementsByClassName("mySlides");
    var dots = document.getElementsByClassName("dot");
    if (n > slides.length) {slideIndex = 1}
    if (n < 1) {slideIndex = slides.length}
    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";
    }
    for (i = 0; i < dots.length; i++) {
        dots[i].className = dots[i].className.replace(" active", "");
    }
    slides[slideIndex-1].style.display = "block";
    dots[slideIndex-1].className += " active";
}