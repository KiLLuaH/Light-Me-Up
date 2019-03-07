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

public class teamRed extends AppCompatActivity {

    public static int redScoreVal = 0;
    public static TextView redScoreText;
    public static TextView redTeamScoreText;
    public static Button redScoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_red);


        redScoreText = (TextView)findViewById(R.id.redScoreText);
        redTeamScoreText = (TextView)findViewById(R.id.redTeamScoreText);
        redScoreButton = (Button)findViewById(R.id.teamRedScoreButton);
        redScoreButton.setEnabled(false);
        redScoreText.setText(getString(R.string.score) + " " + redScoreVal);
        redTeamScoreText.setText(getString(R.string.teamScore) + " " + "0");
    }

    public void redScore (View view) {

        String topic = getString(R.string.topic);
        String payload = getString(R.string.red) + " " + clientId;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

}
