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

public class teamYellow extends AppCompatActivity {

    public static int yellowScoreVal = 0;
    public static TextView yellowScoreText;
    public static TextView yellowTeamScoreText;
    public static Button yellowScoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_yellow);

        yellowScoreButton = (Button) findViewById(R.id.teamYellowScoreButton);
        yellowScoreText = (TextView)findViewById(R.id.yellowScoreText);
        yellowTeamScoreText = (TextView)findViewById(R.id.yellowTeamScoreText);
        yellowScoreButton.setEnabled(false);
        yellowScoreText.setText(getString(R.string.score) + " " + yellowScoreVal);
        yellowTeamScoreText.setText(getString(R.string.teamScore) + " " + "0");

    }

    public void yellowScore (View view) {

        String topic = getString(R.string.topic);
        String team = getString(R.string.yellow);
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
