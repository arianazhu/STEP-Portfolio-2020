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
 * Fetches data and handles response by converting to text for the homepage
 */
function getDataHomepage() {
  fetch('/data').then(response => response.text()).then((messages) => {
    document.getElementById('quote-container').innerText = messages;
    console.log(messages);
  });
}

/**
 * Fetches data and handles response for contact page comments
 */
function getDataComments() {
  fetch('/data').then(response => response.json()).then((comments) => {
      // Build list of comments
      const commentsEl = document.getElementById('comments-list');
      comments.forEach((comment) => {
          commentsEl.appendChild(createListElement(comment));
      });
    // document.getElementById('comments-container').innerText = comments;
    // console.log(comments);
  });
}

/** Creates an <li> element containing text. */
function createListElement(comment) {
    const liElement = document.createElement('li');
    liElement.innerHTML = comment.name + ", " + comment.location + ", " + comment.content;
    return liElement;
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