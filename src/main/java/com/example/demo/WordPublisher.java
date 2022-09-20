package com.example.demo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class WordPublisher {
    protected Word words = new Word();
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value = "/addBad/{word}", method = RequestMethod.POST)
    public ArrayList<String> addBadWord(@PathVariable("word") String s){
        words.badWords.add(s);
        return words.badWords;
    }

    @RequestMapping(value = "/delBad/{word}", method = RequestMethod.POST)
    public ArrayList<String> deleteBadWord(@PathVariable("word") String s){
        words.badWords.remove(String.valueOf(s));
        return words.badWords;
    }

    @RequestMapping(value = "/addGood/{word}", method = RequestMethod.POST)
    public ArrayList<String> addGoodWord(@PathVariable("word") String s){
        words.goodWords.add(s);
        return words.goodWords;
    }

    @RequestMapping(value = "/delGood/{word}", method = RequestMethod.POST)
    public ArrayList<String> deleteGoodWord(@PathVariable("word") String s){
        words.goodWords.remove(String.valueOf(s));
        return words.goodWords;
    }

    @RequestMapping(value = "/proof/{sentence}", method = RequestMethod.POST)
    public String  proofSentence(
            @PathVariable("sentence") String s
    ){
        String msg = "";
        String count = "";
        for(String i : words.goodWords){
            if(s.indexOf(i) !=-1){
                count+="good";
                break;
            }
        }

        for(String i : words.badWords){
            if(s.indexOf(i) !=-1){
                count+="bad";
                break;
            }
        }

        if(count.equals("good")){
            msg += "Found Good word";
            rabbitTemplate.convertAndSend("DirectExchange", "good", s);
        } else if (count.equals("bad")) {
            msg += "Found Bad word";
            rabbitTemplate.convertAndSend("DirectExchange", "bad", s);
        } else if (count.equals("goodbad")) {
            msg += "Found Bad & Good word";
            rabbitTemplate.convertAndSend("FanoutExchange", "", s);
        }
        return msg;
    }
    @RequestMapping(value = "/getSentence", method = RequestMethod.GET)
    public Sentence getSentence(){
       return (Sentence) (rabbitTemplate.convertSendAndReceive("DirectExchange", "GetQueue",""));

    }
}


