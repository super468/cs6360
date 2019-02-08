# README

## PART I

## How to use

1. Maven Build the project
2. Upload the `cs6360-1.0-SNAPSHOT-jar-with-dependencies.jar` to `cs6360.utdallas.edu` server
3. run `hadoop jar cs6360-1.0-SNAPSHOT-jar-with-dependencies.jar part1.PartI <args[0]>`
4. `args[0]` is the path of HDFS filesystem. we set to `hdfs://cshadoop1/user/YourNetID`.


## PART II

## How to use
1. Maven Build the project
2. Upload the `cs6360-1.0-SNAPSHOT-jar-with-dependencies.jar` to `cs6360.utdallas.edu` server
3. run `hadoop jar cs6360-1.0-SNAPSHOT-jar-with-dependencies.jar part2.TwitterDownloader <args[0]> <args[1]>`
4. `args[0]` means how many days you want to search should be `1 <= args[0] <= 6`. we set to `6`
5. `args[1]` means how many queries on each day, we set to `200`.
6. run `hadoop jar cs6360-1.0-SNAPSHOT-jar-with-dependencies.jar part2.CopyFilesToHDFS /home/012/t/tx/txw171430/tweets hdfs://cshadoop1/user/txw171430/tweets`, make sure you have `user/txw171430/tweets` directory on your hdfs


### Program Flow

We used twitter4j as our client to make request calls against twitter standard api.  

We hardcoded the API key and API secret key to get OAuth2Token which is for the request authentication.  

Once we got the token, we are ready to make request calls.  

Because of twitter's limitation, we can get at most 100 tweets per query. There is also a rate limit that twitter does not want you to query too fast. So we can make at most 450 queries in 15 mins window otherwise we should wait to the next window.

We set the maxID to be the smallest ID in the current query results to make sure that next query results's id are smaller than the maxID.

When we got the query result, we stored the text part of them to a text file named as the `fromDate-toDate.txt`.

### TOPIC SEARCHED

nba

### TIMELINES

We searched the tweets by day.  
we accept an argument `X` that has to satisfy `1 <= x <= 6`. 
We started to search from the current date until `x` days ago.
Tweets searched on each day will be stored in a text file.

### FILE SIZE

We made 200 queries on each day, So each file is about 2.5MB.