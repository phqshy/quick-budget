package me.fishy.testapp.app.ui.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import me.fishy.testapp.R;
import me.fishy.testapp.app.ui.fragment.HomeSettingsFragment;
import me.fishy.testapp.app.ui.fragment.payments.PaymentsAddFragment;
import me.fishy.testapp.common.holders.UserDataHolder;
import me.fishy.testapp.common.request.post.JSONPostRequest;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    public static File cacheDirectory;

    //0 = home, 1 = exchange, 2 = payments, 3 = scheduled, 4 = target
    public static int menuMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.mainActionBar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(AppCompatResources.getDrawable(getBaseContext(), R.drawable.ic_hamburger));
        cacheDirectory = this.getCacheDir();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        switch (menuMode){
            case 0:
                getMenuInflater().inflate(R.menu.toolbar_menu, menu);
                initDrawer();
                return true;
            case 1:
                getMenuInflater().inflate(R.menu.toolbar_info, menu);
                initDrawer();
                return true;
            case 2:
                getMenuInflater().inflate(R.menu.toolbar_payment, menu);
                initDrawer();
                return true;
            case 3:
                getMenuInflater().inflate(R.menu.toolbar_payment, menu);
                initDrawer();
                return true;
            case 4:
                getMenuInflater().inflate(R.menu.toolbar_default, menu);
                initDrawer();
                return true;
            default:
                getMenuInflater().inflate(R.menu.toolbar_menu, menu);
                initDrawer();
                return true;
        }
    }

    private void initDrawer(){
        drawer = findViewById(R.id.drawer_layout);
        findViewById(R.id.location_exchange).setOnClickListener(v -> {
            try{
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                switch (menuMode){
                    case 0:
                        navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_exchangeRateFragment);
                        break;
                    case 2:
                        navHostFragment.getNavController().navigate(R.id.action_paymentsFragment_to_exchangeRateFragment);
                        getSupportActionBar().setTitle("Quick Budget");
                        break;
                    case 3:
                        navHostFragment.getNavController().navigate(R.id.action_scheduledPaymentsFragment_to_exchangeRateFragment);
                        getSupportActionBar().setTitle("Quick Budget");
                        break;
                    case 4:
                        navHostFragment.getNavController().navigate(R.id.action_targetSpendingFragment_to_exchangeRateFragment);
                        break;
                    default:
                        return;
                }

                //set menu to exchange
                menuMode = 1;
                invalidateOptionsMenu();
                drawer.closeDrawer(GravityCompat.START);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        findViewById(R.id.location_home).setOnClickListener(v -> {
            try{
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                switch (menuMode){
                    case 1:
                        navHostFragment.getNavController().navigate(R.id.action_exchangeRateFragment_to_homeFragment);
                        break;
                    case 2:
                        navHostFragment.getNavController().navigate(R.id.action_paymentsFragment_to_homeFragment);
                        getSupportActionBar().setTitle("Quick Budget");
                        break;
                    case 3:
                        navHostFragment.getNavController().navigate(R.id.action_scheduledPaymentsFragment_to_homeFragment);
                        getSupportActionBar().setTitle("Quick Budget");
                        break;
                    case 4:
                        navHostFragment.getNavController().navigate(R.id.action_targetSpendingFragment_to_homeFragment);
                        break;
                    default:
                        return;
                }

                //set menu to home
                menuMode = 0;
                invalidateOptionsMenu();
                drawer.closeDrawer(GravityCompat.START);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        findViewById(R.id.location_payment).setOnClickListener(v -> {
            try{
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                switch (menuMode){
                    case 0:
                        navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_paymentsFragment);
                        break;
                    case 1:
                        navHostFragment.getNavController().navigate(R.id.action_exchangeRateFragment_to_paymentsFragment);
                        break;
                    case 3:
                        navHostFragment.getNavController().navigate(R.id.action_scheduledPaymentsFragment_to_paymentsFragment);
                        getSupportActionBar().setTitle("Quick Budget");
                        break;
                    case 4:
                        navHostFragment.getNavController().navigate(R.id.action_targetSpendingFragment_to_paymentsFragment);
                        break;
                    default:
                        return;
                }

                //set menu to payments
                menuMode = 2;
                invalidateOptionsMenu();
                drawer.closeDrawer(GravityCompat.START);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        findViewById(R.id.location_scheduled).setOnClickListener(v -> {
            try{
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                switch (menuMode){
                    case 0:
                        navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_scheduledPaymentsFragment);
                        break;
                    case 1:
                        navHostFragment.getNavController().navigate(R.id.action_exchangeRateFragment_to_scheduledPaymentsFragment);
                        break;
                    case 2:
                        navHostFragment.getNavController().navigate(R.id.action_paymentsFragment_to_scheduledPaymentsFragment);
                        getSupportActionBar().setTitle("Quick Budget");
                        break;
                    case 4:
                        navHostFragment.getNavController().navigate(R.id.action_targetSpendingFragment_to_scheduledPaymentsFragment);
                        break;
                    default:
                        return;
                }

                //set menu to scheduled
                menuMode = 3;
                System.out.println("called main");
                invalidateOptionsMenu();
                drawer.closeDrawer(GravityCompat.START);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        findViewById(R.id.location_target).setOnClickListener(v -> {
            try{
                NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                switch (menuMode){
                    case 0:
                        navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_targetSpendingFragment);
                        break;
                    case 1:
                        navHostFragment.getNavController().navigate(R.id.action_exchangeRateFragment_to_targetSpendingFragment);
                        break;
                    case 2:
                        navHostFragment.getNavController().navigate(R.id.action_paymentsFragment_to_targetSpendingFragment);
                        getSupportActionBar().setTitle("Quick Budget");
                        break;
                    case 3:
                        navHostFragment.getNavController().navigate(R.id.action_scheduledPaymentsFragment_to_targetSpendingFragment);
                        break;
                    default:
                        return;
                }

                //set menu to scheduled
                menuMode = 4;
                System.out.println("called main");
                invalidateOptionsMenu();
                drawer.closeDrawer(GravityCompat.START);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == (int) 16908332) {
            drawer.openDrawer(GravityCompat.START);
            return true;
        }
        switch (item.getItemId()) {
            case 16908332:
                drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_info:
                PopupWindow window = new PopupWindow(getLayoutInflater().inflate(R.layout.exchange_info_popup_layout, null, false), 500, 500, true);
                window.setBackgroundDrawable(null);
                window.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);
                window.showAtLocation(findViewById(R.id.nav_host_fragment), Gravity.CENTER, 0, 0);
                return true;
            case R.id.action_add_payment:
                if (menuMode == 2){
                    if (PaymentsAddFragment.isIsEnabled()) return false;

                    NavHostFragment navHostFragment =
                            (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

                    navHostFragment.getNavController().navigate(R.id.action_paymentsFragment_to_paymentsAddFragment);
                    return true;
                }
                if (menuMode == 3){
                    NavHostFragment navHostFragment =
                            (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

                    navHostFragment.getNavController().navigate(R.id.action_scheduledPaymentsFragment_to_newScheduleFragment);
                    return true;
                }
                return false;
            case R.id.action_settings:
                try {
                    NavHostFragment navHostFragment =
                        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    navHostFragment.getNavController().navigate(R.id.action_homeFragment_to_homeSettingsFragment);
                    return true;
                } catch (Exception e) {
                    return false;
                }

        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Attempting to send data! called from main");
        if (!HomeSettingsFragment.buttoned){
            try {
                String json = UserDataHolder.getGson().toJson(UserDataHolder.getInstance());
                System.out.println(json);
                File stored = new File(getCacheDir() + "/cached_instance.txt");

                if (stored.exists()){
                    PrintWriter pw = new PrintWriter(stored);
                    pw.close();
                }

                System.out.println("Writing stored instance to file. called from main");
                FileWriter writer = new FileWriter(stored);
                writer.write(json);
                writer.close();


                System.out.println("Writing current time in mills to file. called from main");
                File mils = new File(getFilesDir() + "/timeinms.txt");

                if (mils.exists()){
                    PrintWriter pw = new PrintWriter(stored);
                    pw.close();
                }

                FileWriter writer2 = new FileWriter(mils);
                writer2.write(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                writer2.close();

                new JSONPostRequest("https://phqsh.me/update_user")
                        .post(json)
                        .thenAccept(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("is not a button call, setting it to false");
            HomeSettingsFragment.buttoned = false;
        }

    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}
