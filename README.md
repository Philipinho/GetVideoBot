# GetVideoBot
Twitter bot to download videos and gifs.  
Bot can be found at [https://twitter.com/GetVid_](https://twitter.com/GetVid_)

### How it works  
1.	User finds a tweet containing a video or gif.  
2.	User mentions @GetVid_ in a reply  
3.	The bot replies with a link to download the video.  

Replace `src/main/resources/settings.properties.example` with `src/main/resources/settings.properties`.  
```
cp src/main/resources/settings.properties.example src/main/resources/settings.properties
```

The settings are stored inside the `src/main/resources/settings.properties` file.  
Edit the settings.properties file and insert your bot username and Twitter API keys.  

### Twitter API
Replace the value of `twitter.username=` property with your Twitter bot username. Make sure to include the @ before it.  

Replace the value of the following properties with your Twitter Developer API credentials (https://developer.twitter.com/en)  
```
tw.apiKey=
tw.apiSecretKey=
tw.accessToken=
tw.accessTokenSecret=
```

### To build and compile the program   
`mvn clean install`

After a successful build, you can start the program with:  
```
java -jar target/getvid-2.0.jar
```


Use `nohup` to keep the program running even after the terminal is closed:
```
nohup java -jar target/getvid-2.0.jar &
```