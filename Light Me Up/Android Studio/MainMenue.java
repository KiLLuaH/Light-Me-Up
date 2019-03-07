package com.example.abj222.light_me_up;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Scanner;

public class MainMenue extends AppCompatActivity {

    public static MqttAndroidClient client;
    public static String clientId;
    public static MenuItem conDisconItem;
    public static boolean connected = false;

    public static Button teamRedButton;
    public static Button teamBlueButton;
    public static Button teamGreenButton;
    public static Button teamYellowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menue);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        teamRedButton = (Button)findViewById(R.id.teamRedButton);
        teamBlueButton = (Button)findViewById(R.id.teamBlueButton);
        teamGreenButton = (Button)findViewById(R.id.teamGreenButton);
        teamYellowButton = (Button)findViewById(R.id.teamYellowButton);

        hideTeams();

    }

    // Creating a Menue

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        conDisconItem = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }

    // Method to connect and disconnect to the mqtt broker via menu-item

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!connected) {
            connecting();
            clientId = MqttClient.generateClientId();
            client =
                    new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.mqttdashboard.com",
                            clientId);

            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Toast.makeText(MainMenue.this, "Connected!", Toast.LENGTH_LONG).show();
                        showTeams();
                        subscribe();
                        connected();
                        connected = true;
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Toast.makeText(MainMenue.this, "Connection failed!", Toast.LENGTH_LONG).show();
                        disconnected();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                    Toast.makeText(MainMenue.this, "Disconnected!", Toast.LENGTH_LONG).show();
                    hideTeams();
                    disconnected();
                    connected = false;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                    Toast.makeText(MainMenue.this, "Disconnection failed!", Toast.LENGTH_LONG).show();
                }
            });
            } catch (MqttException e) {
            e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to subscribe to the channel in the mqtt broker

    public void subscribe() {

        final String TOPIC = getString(R.string.topic);
        final String SCORE = getString(R.string.score);
        final String TEAM_SCORE = getString(R.string.teamScore);
        final String ADMIN = getString(R.string.admin);
        final String READY = getString(R.string.ready);
        final String START = getString(R.string.start);
        final String STOP = getString(R.string.stop);
        final String RED = getString(R.string.red);
        final String BLUE = getString(R.string.blue);
        final String GREEN = getString(R.string.green);
        final String YELLOW = getString(R.string.yellow);

        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(TOPIC, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            // Method to handle the incoming messages from the mqtt broker

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                Scanner scanner = new Scanner(msg);
                String task = scanner.next();
                String content = "";

                if (task.contentEquals(ADMIN)) {
                    content = scanner.next();
                    if (content.contentEquals(READY)) {
                        setReady();
                    } else if (content.contentEquals(START)) {
                        teamRed.redScoreButton.setEnabled(true);
                        teamBlue.blueScoreButton.setEnabled(true);
                        teamGreen.greenScoreButton.setEnabled(true);
                        teamYellow.yellowScoreButton.setEnabled(true);
                    } else if (content.contentEquals(STOP)) {
                        teamRed.redScoreButton.setEnabled(false);
                        teamBlue.blueScoreButton.setEnabled(false);
                        teamGreen.greenScoreButton.setEnabled(false);
                        teamYellow.yellowScoreButton.setEnabled(false);
                    }
                } else if (task.contentEquals(RED)) {
                    content = scanner.next();
                    if (content.contentEquals(clientId)) {
                        teamRed.redScoreVal++;
                        teamRed.redScoreText.setText(SCORE + " " + teamRed.redScoreVal);
                    }
                } else if (task.contentEquals(BLUE)) {
                    content = scanner.next();
                    if (content.contentEquals(clientId)) {
                        teamBlue.blueScoreVal++;
                        teamBlue.blueScoreText.setText(SCORE + " " + teamBlue.blueScoreVal);
                    }
                } else if (task.contentEquals(GREEN)) {
                    content = scanner.next();
                    if (content.contentEquals(clientId)) {
                        teamGreen.greenScoreVal++;
                        teamGreen.greenScoreText.setText(SCORE + " " + teamGreen.greenScoreVal);
                    }
                } else if (task.contentEquals(YELLOW)) {
                    content = scanner.next();
                    if (content.contentEquals(clientId)) {
                        teamYellow.yellowScoreVal++;
                        teamYellow.yellowScoreText.setText(SCORE + " " + teamYellow.yellowScoreVal);
                    }
                } else if (task.contentEquals("R")) {
                    content = scanner.next();
                    teamRed.redTeamScoreText.setText(TEAM_SCORE + " " + content);
                } else if (task.contentEquals("B")) {
                    content = scanner.next();
                    teamBlue.blueTeamScoreText.setText(TEAM_SCORE + " " + content);
                } else if (task.contentEquals("G")) {
                    content = scanner.next();
                    teamGreen.greenTeamScoreText.setText(TEAM_SCORE + " " + content);
                } else if (task.contentEquals("Y")) {
                    content = scanner.next();
                    teamYellow.yellowTeamScoreText.setText(TEAM_SCORE + " " + content);
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    // Method to reset all values to prepare next round

    public void setReady() {
        // Red-Team
        teamRed.redScoreVal = 0;
        teamRed.redScoreText.setText("Your Score: " + teamRed.redScoreVal);
        teamRed.redTeamScoreText.setText("Team Score: " + "0");
        teamRed.redScoreButton.setEnabled(false);

        // Blue-Team
        teamBlue.blueScoreVal = 0;
        teamBlue.blueScoreText.setText("Your Score: " + teamRed.redScoreVal);
        teamBlue.blueTeamScoreText.setText("Team Score: " + "0");
        teamBlue.blueScoreButton.setEnabled(false);

        // Green-Team
        teamGreen.greenScoreVal = 0;
        teamGreen.greenScoreText.setText("Your Score: " + teamRed.redScoreVal);
        teamGreen.greenTeamScoreText.setText("Team Score: " + "0");
        teamGreen.greenScoreButton.setEnabled(false);

        // Yellow-Team
        teamYellow.yellowScoreVal = 0;
        teamYellow.yellowScoreText.setText("Your Score: " + teamRed.redScoreVal);
        teamYellow.yellowTeamScoreText.setText("Team Score: " + "0");
        teamYellow.yellowScoreButton.setEnabled(false);
    }

    // Settings of buttons

    public void hideTeams() {
        teamRedButton.setEnabled(false);
        teamBlueButton.setEnabled(false);
        teamGreenButton.setEnabled(false);
        teamYellowButton.setEnabled(false);
        teamRedButton.setAlpha(0.25f);
        teamBlueButton.setAlpha(0.25f);
        teamGreenButton.setAlpha(0.25f);
        teamYellowButton.setAlpha(0.25f);
        teamRedButton.setBackgroundColor(getResources().getColor(R.color.grey));
        teamBlueButton.setBackgroundColor(getResources().getColor(R.color.grey));
        teamGreenButton.setBackgroundColor(getResources().getColor(R.color.grey));
        teamYellowButton.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    public void showTeams() {
        teamRedButton.setEnabled(true);
        teamBlueButton.setEnabled(true);
        teamGreenButton.setEnabled(true);
        teamYellowButton.setEnabled(true);
        teamRedButton.setAlpha(1);
        teamBlueButton.setAlpha(1);
        teamGreenButton.setAlpha(1);
        teamYellowButton.setAlpha(1);
        teamRedButton.setBackgroundColor(getResources().getColor(R.color.red));
        teamBlueButton.setBackgroundColor(getResources().getColor(R.color.blue));
        teamGreenButton.setBackgroundColor(getResources().getColor(R.color.green));
        teamYellowButton.setBackgroundColor(getResources().getColor(R.color.yellow));
    }

    // Menu-Item-Handler

    public void connected() {
        conDisconItem.setTitle(R.string.disconnect);
        conDisconItem.setIcon(R.drawable.icon_connected);
    }

    public void connecting() {
        conDisconItem.setTitle(R.string.connecting);
        conDisconItem.setIcon(R.drawable.icon_searching);
    }

    public void disconnected() {
        conDisconItem.setTitle(R.string.connect);
        conDisconItem.setIcon(R.drawable.icon_disconnected);
    }

    // Button-Listener

    public void red(View view) {
        Intent i = new Intent(this, teamRed.class);
        startActivity(i);
    }

    public void blue(View view) {
        Intent i = new Intent(this, teamBlue.class);
        startActivity(i);
    }

    public void green(View view) {
        Intent i = new Intent(this, teamGreen.class);
        startActivity(i);
    }

    public void yellow(View view) {
        Intent i = new Intent(this, teamYellow.class);
        startActivity(i);
    }

}
