package Messages;

import coen445.client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * Created by Ahmed on 15-12-03.
 */
public class WithdrawMessage extends UDPMessage {

    private static final long serialVersionUID = 7526472295622776147L;


    private int meetingNumber;



    private InetAddress inetAddress;

    public WithdrawMessage(){

        setType("Withdraw");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(!meetingNumberReady(br));
    }

    public WithdrawMessage(int meetingNumber, InetAddress address){
        this.meetingNumber = meetingNumber;
        this.inetAddress = address;
    }

    public int getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(int meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }
    private boolean meetingNumberReady(BufferedReader br) {

        try {
            System.out.println("Please enter the meeting number to withdraw from");
            if(Client.meetingNumberToMeetingData.containsKey(meetingNumber)){
                System.out.println("This meeting is  currently scheduled and you are able to withdraw");
                setMeetingNumber(Integer.parseInt(br.readLine()));
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        catch (NumberFormatException e) {
            System.out.println("Not an Integer");
            return false;
        }
        return true;
    }

    @Override
    public void displayMessage() {
        super.displayMessage();
        System.out.println("|| " + getType() + " | " + getMeetingNumber() + " | " + getInetAddress()+ "||");


    }
}
