package bk.devoxx17.utils;

import java.util.Timer;
import java.util.TimerTask;

public class DownloadTimer {
    private int minutes;
    private int seconds;
    private Timer innerTimer = new Timer();
    private TimerTask innerTask;
    private boolean isActive;
    private boolean gameOver = false;

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean b){
        gameOver = b;
    }

    public DownloadTimer(int minutes, int seconds) {
        resetTimer(minutes, seconds);
    }

    public void start() {
        //if(gameOver)
        innerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(getTime());
                isActive = true;
                if (seconds == 0 && minutes > 0){
                    minutes -= 1;
                    seconds = 59;
                } else if (seconds == 0 && minutes == 0){
                    isActive = false;
                    //innerTimer.cancel();
                    //innerTimer.purge();
                    gameOver = true;

                } else {
                    seconds -= 1;
                }
            }
        };
        innerTimer.scheduleAtFixedRate(innerTask, 0, 1000);
    }

    public void stop() {
        innerTask.cancel();
    }

    public String getTime(){
        String formatMin = String.format("%02d", minutes);
        String formatSec = String.format("%02d", seconds);
        String formatRet = formatMin + ":" + formatSec;

        return formatRet;

        //return ("" + minutes + ":" + seconds);
    }

    public boolean getIsActive(){
        return this.isActive;
    }

    public void resetTimer(int minutes, int seconds){
        gameOver = false;
        if (seconds > 60) {
            int minToAdd = seconds / 60;
            this.minutes = minutes;
            this.minutes += minToAdd;
            this.seconds = seconds % 60;
        } else {
            this.minutes = minutes;
            this.seconds = seconds;
        }
    }
}
