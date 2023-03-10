package com.kardelen.rtsp_player;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;


public class Rtsp {


    private String movie_file;
    private String ip_addr;
    private int port;
    private int c_seq;
    private int session;
    private int id; //The RTSP ID given by the server
    private BufferedReader RTSPBufferedReader;
    private BufferedWriter RTSPBufferedWriter;
    private InetAddress addr;

    private Socket rtsp_socket;

    //Constructor
    public Rtsp(String movie_file, String ip_addr, int port) throws IOException {
        this.movie_file = movie_file;
        this.ip_addr = ip_addr;
        this.port = port;
        this.c_seq = 1;
        this.id = 0;
        //Session given from setup
        this.session = 0;
        this.addr = InetAddress.getByName(ip_addr);

        //Create RTSP Socket
        this.rtsp_socket = new Socket(ip_addr,port);
        //Create RTSP Writers/Readers
        RTSPBufferedReader = new BufferedReader(new InputStreamReader(rtsp_socket.getInputStream()) );
        RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(rtsp_socket.getOutputStream()) );
    }

    //SETUP
    public boolean setup(){

        //Create RTSP Message
        String rtsp_setup = "SETUP rtsp://" + ip_addr + ":" + port + "/" + movie_file + " RTSP/1.0" + "\nCSeq: " + c_seq +"\n"+"Transport: RTP/UDP; client_port= " + 25000;
        c_seq++;

        //Create & Send Packet
        try{
            RTSPBufferedWriter.write(rtsp_setup);
            RTSPBufferedWriter.flush();
        }catch(Exception e){
            return false;
        }

        //Receive response
        if(getRTSPResponse() == 200)
            return true;
        else
            return false;
    }

    public boolean play(){
        //Create RTSP Message
        String rtsp_play = "PLAY rtsp://" + ip_addr + ":" + port + "/" + movie_file + " RTSP/1.0" + "\nCSeq: " + c_seq +"\n"+"Session: " + id;
        c_seq++;

        //Create & Send Packet
        try{
            RTSPBufferedWriter.write(rtsp_play);
            RTSPBufferedWriter.flush();
        }catch(Exception e){
            return false;
        }

        //Receive Response
        if(getRTSPResponse()==200)
            return true;
        else
            return false;
    }

    public boolean pause(){
        //Create RTSP Message
        String rtsp_pause = "PAUSE rtsp://" + ip_addr + ":" + port + "/" + movie_file + " RTSP/1.0" + "\nCSeq: " + c_seq +"\n"+"Session: " + id;
        c_seq++;

        //Create and send packet
        try{
            RTSPBufferedWriter.write(rtsp_pause);
            RTSPBufferedWriter.flush();
        }catch(Exception e){
            return false;
        }

        //Receive Response
        if(getRTSPResponse() == 200)
            return true;
        else
            return false;
    }

    public boolean teardown(){
        //Create RTSP Message
        String rtsp_teardown = "TEARDOWN rtsp://" + ip_addr + ":" + port + "/" + movie_file + " RTSP/1.0" + "\nCSeq: " + c_seq +"\n"+"Session: " + id;
        c_seq++;

        //Create and send packet
        try{
            RTSPBufferedWriter.write(rtsp_teardown);
            RTSPBufferedWriter.flush();
        }catch(Exception e){
            return false;
        }

        //Receive Response
        if(getRTSPResponse() == 200)
            return true;
        else
            return false;
    }



    //Gets the return code
    private int getRTSPResponse(){
        int reply = 0;
        try{
            //Get Response
            String status = RTSPBufferedReader.readLine();
            StringTokenizer tokens = new StringTokenizer(status);
            tokens.nextToken();
            reply = Integer.parseInt(tokens.nextToken());
        }catch(Exception e){
            reply = -1;
        }

        //Parse the response to get ID
        try{
            String seq = RTSPBufferedReader.readLine();
            String session = RTSPBufferedReader.readLine();
            StringTokenizer tokens = new StringTokenizer(session);
            tokens.nextToken();
            id = Integer.parseInt(tokens.nextToken());
        }catch(Exception e){
            reply = -1;
        }

        return reply;
    }



    //Getters and Setters
    public String getMovie_file(){
        return movie_file;
    }
    public void setMovie_file(String movie_file){
        this.movie_file = movie_file;
    }

    public String getIp_addr(){
        return ip_addr;
    }
    public void setIp_addr(String ip_addr){
        this.ip_addr = ip_addr;
    }

    public int getPort(){
        return port;
    }
    public void setPort(int port){
        this.port = port;
    }



}
