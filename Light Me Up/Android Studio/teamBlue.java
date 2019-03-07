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

public class teamBlue extends AppCompatActivity {

    public static int blueScoreVal = 0;
    public static TextView blueScoreText;
    public static TextView blueTeamScoreText;
    public static Button blueScoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_blue);

        blueScoreButton = (Button) findViewById(R.id.teamBlueScoreButton);
        blueScoreText = (TextView)findViewById(R.id.blueScoreText);
        blueTeamScoreText = (TextView)findViewById(R.id.blueTeamScoreText);
        blueScoreButton.setEnabled(false);
        blueScoreText.setText(getString(R.string.score) + " " + blueScoreVal);
        blueTeamScoreText.setText(getString(R.string.teamScore) + " " + "0");

    }

    public void blueScore(View view) {

        String topic = getString(R.string.topic);
        String team = getString(R.string.blue);
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
