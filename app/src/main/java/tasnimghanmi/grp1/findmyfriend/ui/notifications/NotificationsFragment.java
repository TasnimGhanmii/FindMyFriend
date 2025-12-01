package tasnimghanmi.grp1.findmyfriend.ui.notifications;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import tasnimghanmi.grp1.findmyfriend.Config;
import tasnimghanmi.grp1.findmyfriend.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private EditText pseudo, numero, longitude, latitude;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pseudo = binding.pseudo;
        numero = binding.numero;
        longitude = binding.longitude;
        latitude = binding.latitudee;

        binding.addbtnn.setOnClickListener(v -> {
            String pseudoStr = pseudo.getText().toString().trim();
            String numeroStr = numero.getText().toString().trim();
            String lonStr = longitude.getText().toString().trim();
            String latStr = latitude.getText().toString().trim();

            if (pseudoStr.isEmpty() || lonStr.isEmpty() || latStr.isEmpty()) {
                Toast.makeText(getContext(), "Pseudo, longitude et latitude requis", Toast.LENGTH_SHORT).show();
                return;
            }

            new AddPositionTask().execute(pseudoStr, lonStr, latStr, numeroStr);
        });

        return root;
    }

    private class AddPositionTask extends AsyncTask<String, Void, String> {
        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Ajout en cours...");
            builder.setMessage("Veuillez patienter...");
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String pseudo = params[0];
            String longitude = params[1];
            String latitude = params[2];
            String numero = params[3];

            try {

                URL url = new URL(Config.URL_add_location);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "pseudo=" + java.net.URLEncoder.encode(pseudo, "UTF-8") +
                        "&longitude=" + java.net.URLEncoder.encode(longitude, "UTF-8") +
                        "&latitude=" + java.net.URLEncoder.encode(latitude, "UTF-8") +
                        "&numero=" + java.net.URLEncoder.encode(numero.isEmpty() ? "" : numero, "UTF-8");

                try (OutputStream os = new BufferedOutputStream(conn.getOutputStream())) {
                    os.write(postData.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream()
                        )
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                conn.disconnect();

                return "Code: " + responseCode + " | Réponse: " + response.toString();

            } catch (Exception e) {
                return "Erreur réseau: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

            if (result.contains("succès") || result.contains("201") || result.contains("Created")) {
                // Clear fields on success
                pseudo.setText("");
                numero.setText("");
                longitude.setText("");
                latitude.setText("");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}