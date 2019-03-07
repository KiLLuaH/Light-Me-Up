package com.example.abj222.light_me_up;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import static com.example.abj222.light_me_up.MainMenue.client;
import static com.example.abj222.light_me_up.MainMenue.clientId;

public class teamGreen extends AppCompatActivity {

    public static int greenScoreVal = 0;
    public static TextView greenScoreText;
    public static TextView greenTeamScoreText;
    public static Button greenScoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_green);

        greenScoreButton = (Button) findViewById(R.id.teamGreenScoreButton);
        greenScoreText = (TextView)findViewById(R.id.greenScoreText);
        greenTeamScoreText = (TextView)findViewById(R.id.greenTeamScoreText);
        greenScoreButton.setEnabled(false);
        greenScoreText.setText(getString(R.string.score) + " " + greenScoreVal);
        greenTeamScoreText.setText(getString(R.string.teamScore) + " " + "0");

    }

    public void greenScore (View view) {

        String topic = getString(R.string.topic);
        String team = getString(R.string.green);
        String payload = team + " " + clientId;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            //message.setRetained(true);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

}
