# Job notification bot

[![Static Badge](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Static Badge](https://img.shields.io/badge/SpringBoot-3-green.svg)](https://docs.spring.io/spring-boot/docs/3.1.5/reference/html/)

## Description
This project aims to make the process of job searching easier by automating the collection of vacancies from various company websites.
With this system, users can effortlessly stay updated on job openings without the hassle of manually scouring multiple websites.
Our project includes a robust library that allows users to create custom parsers for vacancy sites of their choice.
This flexibility ensures compatibility with a wide range of company websites, catering to diverse job seekers' needs.
Also, users can subscribe to specific companies of interest, enabling the parser to automatically detect and notify them of new job postings.

Available via Telegram Bot: https://t.me/vacancy_notification_bot

## Install
1. Clone the repository: <p><code>git clone https://github.com/yehortpk/job-notification-bot.git</code>;</p>
2. Create a new Telegram bot through <a href="https://t.me/BotFather">Bot Father</a>;
3. Extract API Token and Username of the bot;
4. Create .env.local environment file with the next variables:
   - MYSQL_USERNAME - username that will be used to access <i>notifier</i> db;
   - MYSQL_PASSWORD - password that will be used to access <i>notifier</i> db;
   - TG_BOT_API_KEY - API key from your bot extracted on previous step;
   - TG_BOT_USERNAME - username of your bot extracted on previous step.

5. Create and run a docker project (if you do not have docker installed, you can see the instructions 
<a href="https://docs.docker.com/get-docker/">here</a>) <p><code>docker compose --profile=app --env-file=.env.local -f docker-compose.local.yaml up --build</code></p>

## Usage
The app needs the client to create parsing information for both sides - in the code itself and in the database. Let's look at the features of the code.

### Code usage
There are 3 type of web apps that can be parsed by implementing the next abstract classes:
- <b>ComponentSiteParser</b> - based on component single-page web apps, DOM structure of which is rendered in-time.
  Can't be parsed by default request-based parser, so we use Selenium library here. You need to implement <i>createDynamicElementQuerySelector</i> method for specify
  a query selector for the element that can only be available after the page rendering. Most widely used #root or #app sections
- <b>XHRSiteParser</b> - based on web apps that used async XHR requests to retrieve information from remote server in-time and update it on the page. You may also override
  <i>getConnectionMethod</i> which by default is GET, but many sites use POST as well.
- <b>MultiPageSiteParser</b> - default web apps with a common page design and pagination.

Regardless of the web app type you, as a client, need to implement several methods for the site parser to work. All these methods require a css query selector for find the specific DOM elements.

There are the methods:
- <i>getPagesCount(page: Document): int </i> - based on the first page object, you have to retrieve information about number of pages in the app;
- <i>extractVacancyBlocksFromPage(page: Document): List[Element] </i> - based on the page object, you have to retrieve all the blocks with vacancy on the page;
- <i>generateVacancyObjectFromBlock(block: Element): VacancyDTO </i> - based on the vacancy block, you have to retrieve all the vacancy information from it.

Additionally, you need to create new site parsers as Spring Beans with @Component or @Bean and explicitly specify the 
bean name (this will be used to associate this Java bean class with an object inside the database, which will be mentioned later).

### Database usage
To add a new site for analysis, all you have to do is add the entity to the <i>company</i> table inside the 
<i>notifier</i> database. The database and table <i>company</i> will be created automatically after the first launch of
the application. You must fill out the following fields:
- bean_class - name of the @Bean in the code that corresponds this db entity;
- is_enabled - enable/disable specific parser;
- main_page_url - URL to the site main page;
- vacancies_page_url - URL to the page with the vacancies in the site;
- vacancies_api_url - URL to the endpoint for XHR to retrieve vacancies (only for <i>XHRSiteParser</i>)
- title - company title.

If you have request data that need to be sent with the request (e.g. GET request query data, or POST request body)
you may add this data in the <i>company_data</i> table. 
Also, if you have special headers you want to send with every request, you may add them by creating new key-value pair in
<i>company_headers</i> table.
