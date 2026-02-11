package com.example.graduationproject.ui.teacherUi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.graduationproject.R;
import com.example.graduationproject.adapters.MatchingTeacherAdapter;
import com.example.graduationproject.adapters.TeacherCoursesAdapter;
import com.example.graduationproject.adapters.TeacherReceivedRequestAdapter;
import com.example.graduationproject.databinding.ConfirmDeleteDialogLayoutBinding;
import com.example.graduationproject.databinding.CourseDeclinedNotificationForTeacherLayoutBinding;
import com.example.graduationproject.databinding.DeleteParentCourseCardForParentBinding;
import com.example.graduationproject.databinding.DeleteTeacherCourseCardForTeacherBinding;
import com.example.graduationproject.databinding.DialogTeacherMatchingOnCardClickedBinding;
import com.example.graduationproject.databinding.FilterLayoutBinding;
import com.example.graduationproject.databinding.TeacherCourseCardClickedLayoutBinding;
import com.example.graduationproject.databinding.TeacherPostedRequestCardLayoutBinding;
import com.example.graduationproject.databinding.TeacherReceivedRequestCardLayoutBinding;
import com.example.graduationproject.databinding.TeacherReceivedRequestsDialogLayoutBinding;
import com.example.graduationproject.databinding.UpdatePostedTeacherLookForAJobLayoutBinding;
import com.example.graduationproject.listeners.DeletePostedRequestListener;
import com.example.graduationproject.listeners.FetchCoursesListener;
import com.example.graduationproject.listeners.OnAcceptDeclineTeacherRequestsListener;
import com.example.graduationproject.listeners.OnCheckIfRequestSentBeforeListener;
import com.example.graduationproject.listeners.OnCourseAddedListener;
import com.example.graduationproject.listeners.OnCourseDeclinedFetchedListener;
import com.example.graduationproject.listeners.OnCourseFetchedForParentListener;
import com.example.graduationproject.listeners.OnParentSentRequestFetchListener;
import com.example.graduationproject.listeners.OnRemoveRequestSentListener;
import com.example.graduationproject.listeners.OnSetCourseDoneListener;
import com.example.graduationproject.listeners.OnTeacherCoursesFetchedForConflictListener;
import com.example.graduationproject.listeners.OnTeacherCoursesFetchedListener;
import com.example.graduationproject.listeners.OnTeacherCoursesReceivedListener;
import com.example.graduationproject.listeners.OnTeacherPostRequestUpdateListener;
import com.example.graduationproject.listeners.OnReceivedRequestsListener;
import com.example.graduationproject.listeners.OnSentRequestDeletedListener;
import com.example.graduationproject.listeners.OnTeacherToParentRequestSentListener;
import com.example.graduationproject.listeners.ParentInformationListener;
import com.example.graduationproject.listeners.TeacherCourseClickListener;
import com.example.graduationproject.listeners.TeacherPostRequestClickListener;
import com.example.graduationproject.database.Database;
import com.example.graduationproject.databinding.FragmentTeacherBinding;
import com.example.graduationproject.errorHandling.MyAlertDialog;
import com.example.graduationproject.listeners.AddTeacherMatchingListener;
import com.example.graduationproject.listeners.PostedTeacherRequestsListener;
import com.example.graduationproject.listeners.TeacherMatchCardClickListener;
import com.example.graduationproject.adapters.TeacherPostedRequestsAdapter;
import com.example.graduationproject.messaging.ChatWindowActivity;
import com.example.graduationproject.messaging.ChatWindowActivity2;
import com.example.graduationproject.messaging.Users;
import com.example.graduationproject.models.Address;
import com.example.graduationproject.models.Children;
import com.example.graduationproject.models.Course;
import com.example.graduationproject.models.CustomChildData;
import com.example.graduationproject.models.DateTimeModel;
import com.example.graduationproject.models.ExpiredCourse;
import com.example.graduationproject.models.FilterCriteria;
import com.example.graduationproject.models.Notifications;
import com.example.graduationproject.models.Parent;
import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.models.TeacherPostRequest;
import com.example.graduationproject.models.TeacherReceivedRequest;
import com.example.graduationproject.ui.parentUi.ParentFragment;
import com.example.graduationproject.utils.DateUtils;
import com.example.graduationproject.utils.FilterData;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeacherFragment extends Fragment implements TeacherMatchCardClickListener,
        AddTeacherMatchingListener,
        PostedTeacherRequestsListener,
        TeacherPostRequestClickListener, OnTeacherPostRequestUpdateListener,
        DeletePostedRequestListener, ParentInformationListener,
        OnReceivedRequestsListener, OnAcceptDeclineTeacherRequestsListener,
        OnTeacherCoursesReceivedListener, OnCourseAddedListener, OnTeacherToParentRequestSentListener,
        OnCheckIfRequestSentBeforeListener,
        OnSentRequestDeletedListener, OnTeacherCoursesFetchedForConflictListener, OnTeacherCoursesFetchedListener, FetchCoursesListener, TeacherCourseClickListener, OnSetCourseDoneListener, OnRemoveRequestSentListener, OnCourseFetchedForParentListener, OnCourseDeclinedFetchedListener {

    private FragmentTeacherBinding binding;

    private Database database;
    String email;
    String teacherName;

    private ArrayList<TeacherMatchModel> parentPostedRequestsForTeacherList = new ArrayList<>();
    private MatchingTeacherAdapter matchingTeacherAdapter;
    private boolean myCoursesBtnForParent = true;
    private boolean myPostedRequestsBtnForTeacher = false;
    private boolean isBroadcastReceiverRegistered = false;
    boolean browseParentPostedRequestsBtnForTeacher = false;
    private boolean showTeacherReceivedRequestBtn = false;
    List<TeacherPostRequest> teacherPostedRequestsList = new ArrayList<>();

    private TeacherPostedRequestsAdapter teacherPostedRequestsAdapter;

    List<String> teacherPhoneNumbersList = new ArrayList<>();
    List<Address> teacherAddressesList = new ArrayList<>();
    TeacherPostRequest newTeacherRequest;
    Dialog teacherPostedRequestCardDialog;
    boolean isTeacherPostedRequestCardDialogShowing = false;
    private TeacherPostedRequestCardLayoutBinding teacherPostedRequestCardLayoutBinding;
    String parentFirstName = "";
    String parentLastName = "";
    private String amPmStart, amPmEnd;
    private String startTime = "12:00 PM", endTime = "12:00 PM";
    UpdatePostedTeacherLookForAJobLayoutBinding updatePostedTeacherLookForAJobLayoutBinding;
    private Dialog updatePostedRequestDialog;
    private List<String> coursesList = new ArrayList<>();
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
    private String teacherAvailability;
    private TeacherMatchModel tempTeacherMatchModel;


    private TeacherReceivedRequestAdapter teacherReceivedRequestAdapter;
    private Dialog deleteRequestConfirmationDialog;
    private Dialog teacherReceivedRequestDialog;
    TeacherReceivedRequestsDialogLayoutBinding teacherReceivedRequestsDialogLayoutBinding;
    private int tempTeacherPostId, tempRequestId;
    private DateTimeModel currentRequestDate;
    private TeacherReceivedRequest tempTeacherReceivedRequestObject;
    private List<TeacherReceivedRequest> tempTeacherReceivedRequestsList;
    private Dialog parentPostedRequestsForTeacherDialog;
    private DialogTeacherMatchingOnCardClickedBinding dialogTeacherMatchingOnCardClickedBinding;
    private TeacherMatchModel tempTeacherMatchModelForCheckTeacherSentRequest;
    private Dialog newTeacherReceivedRequestDialog;
    private TeacherReceivedRequestCardLayoutBinding teacherReceivedRequestCardLayoutBinding;
    private TeacherReceivedRequest tempTeacherReceivedRequest;
    private List<TeacherReceivedRequest> tempOneRequestList = new ArrayList<>();
    private FilterLayoutBinding filterLayoutBinding;
    private Dialog filterDialog;
    private final List<String> filterSelectedLocationList = new ArrayList<>();
    private final List<String> filterSelectedCoursesList = new ArrayList<>();
    private final List<String> filterSelectedGenderList = new ArrayList<>();
    private final List<String> filterSelectedGradeList = new ArrayList<>();
    private final FilterData filterDataObject = new FilterData();
    private final List<String> filterTeachingMethodList = new ArrayList<>();
    private boolean isFilterDialogShowing = false ;
    private DateTimeModel tempRequestToSendDateTimeModel;
    private TeacherMatchModel currentCourseToSendRequestMatchModel ;
    private List<Course> teacherCoursesList = new ArrayList<>();
    private List<ExpiredCourse> expiredCoursesList = new ArrayList<>();
    private TeacherCoursesAdapter teacherCoursesAdapter;
    private Dialog courseDialog;
    String receiverEmail;

    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("UPDATE_TEACHER_POSTED_REQUESTS".equals(intent.getAction())) {
                if(myPostedRequestsBtnForTeacher && !myCoursesBtnForParent && !browseParentPostedRequestsBtnForTeacher)
                    btnMyPostedRequestsClicked();
            }

            else if ("SHOW_PARENT_POSTED_REQUESTS_FOR_TEACHER".equals(intent.getAction())) {
                browseParentPostedRequestsBtnForTeacher = true;
                database.getTeacherMatchingData(email, TeacherFragment.this);
                binding.filterLayout.setVisibility(View.VISIBLE);
            }

            else if ("SHOW_TEACHER_RECEIVED_REQUESTS".equalsIgnoreCase(intent.getAction())) {
                showTeacherReceivedRequestBtn = true;
                database.getTeacherReceivedRequests(email, TeacherFragment.this);
            }


            else if("SHOW_DELETE_COURSE_REQUEST_FOR_TEACHER".equalsIgnoreCase(intent.getAction())){
                Notifications notification ;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    notification = intent.getParcelableExtra("notification",Notifications.class);
                }
                else {
                    notification = intent.getParcelableExtra("notification");
                }
                if(notification != null){
                    database.getSpecificParentCourse(notification.getParentSentRequestId(), TeacherFragment.this);
                }
                else{
                    MyAlertDialog.showCustomAlertDialogSpinnerError(getContext(),"Unable to show","Unable to show the request data , please communicate with the teacher..");
                }
            }

            else if("SHOW_DELETE_DECLINED_COURSE_REQUEST_FOR_TEACHER".equalsIgnoreCase(intent.getAction())){
                Notifications notification ;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    notification = intent.getParcelableExtra("notification",Notifications.class);
                }
                else {
                    notification = intent.getParcelableExtra("notification");
                }
                if(notification != null){
                    database.getSpecificParentDeclinedCourse(notification.getParentSentRequestId(),
                            notification.getTeacherSentRequestId(),
                            notification.getTempCourseId(), TeacherFragment.this);
                }
                else{
                    MyAlertDialog.showCustomAlertDialogSpinnerError(getContext(),"Unable to show","Unable to show the course data , try again later..");
                }
            }






//            /*else if("TEACHER_RECEIVED_REQUEST_NOTIFICATION_CLICKED".equalsIgnoreCase(intent.getAction())){
//                Toast.makeText(getContext(), "Show Teacher Received Request ..", Toast.LENGTH_SHORT).show();
//                Notifications notification;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//                     notification = intent.getParcelableExtra("notification",Notifications.class);
//                }
//                else {
//                    notification = intent.getParcelableExtra("notification");
//                }
//                if(notification != null){
//                    showTeacherReceivedRequestBtn = true;
//                    flagX=1 ;
//                    //database.getTeacherReceivedRequests(email,TeacherFragment.this);
//                    database.getTeacherSpecificReceivedRequest(notification);
//                    currentNotificationId = notification.getParentSentRequestId();
//                }
//                else {
//                    flagX = 0;
//                    MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","Something Went Wrong , try again later or check the received request from the navigation ..");
//                }
//            }*/
        }
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeacherBinding.inflate(getLayoutInflater(), container, false);
        getTeacherDataFromActivity();
        init();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isBroadcastReceiverRegistered) {
            IntentFilter parentFragmentIntentFilter = new IntentFilter();
            parentFragmentIntentFilter.addAction("UPDATE_TEACHER_POSTED_REQUESTS");
            //parentFragmentIntentFilter.addAction("SHOW_AVAILABLE_JOBS_FOR_TEACHER");
            parentFragmentIntentFilter.addAction("SHOW_PARENT_POSTED_REQUESTS_FOR_TEACHER");
            parentFragmentIntentFilter.addAction("SHOW_TEACHER_RECEIVED_REQUESTS");
            parentFragmentIntentFilter.addAction("TEACHER_RECEIVED_REQUEST_NOTIFICATION_CLICKED");
            parentFragmentIntentFilter.addAction("SHOW_DELETE_COURSE_REQUEST_FOR_TEACHER");
            parentFragmentIntentFilter.addAction("SHOW_DELETE_DECLINED_COURSE_REQUEST_FOR_TEACHER");
            int flags = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                flags = Context.RECEIVER_NOT_EXPORTED;
            }
            if (getActivity() != null) {
                getActivity().registerReceiver(myBroadcastReceiver, parentFragmentIntentFilter, flags);
                isBroadcastReceiverRegistered = true;
            }
        }
    }

    private void updatePostRequestsAdapter() {
        teacherPostedRequestsList.add(newTeacherRequest);
        teacherPostedRequestsAdapter.filteredList(teacherPostedRequestsList);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBroadcastReceiverRegistered && getActivity() != null) {
            getActivity().unregisterReceiver(myBroadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBroadcastReceiverRegistered && getActivity() != null) {
            getActivity().unregisterReceiver(myBroadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }
    }

    private void init() {
        initDatabase();
        initCoursesRecyclerView();
        btnMyCoursesClicked();
        binding.myCoursesBtn.setOnClickListener(x -> {
            btnMyCoursesClicked();
            binding.filterLayout.setVisibility(View.GONE);
        });
        binding.myPostedRequestsBtn.setOnClickListener(c -> {
            btnMyPostedRequestsClicked();
            binding.filterLayout.setVisibility(View.GONE);
        });
        binding.refreshRecyclerView.setOnRefreshListener(() -> {
            if (browseParentPostedRequestsBtnForTeacher && !myCoursesBtnForParent && !myPostedRequestsBtnForTeacher) {
                sendRefreshBroadcast(3); // update available job for teacher ..
            } else if (!browseParentPostedRequestsBtnForTeacher && myCoursesBtnForParent && !myPostedRequestsBtnForTeacher) {
                sendRefreshBroadcast(1);
            } else if (!browseParentPostedRequestsBtnForTeacher && !myCoursesBtnForParent && myPostedRequestsBtnForTeacher) {
                sendRefreshBroadcast(2);
            }
        });
        binding.filterLayout.setOnClickListener(z -> {
            if(!isFilterDialogShowing)
                showFilterDialogForTeacher();
        });
    }

    private void btnMyCoursesClicked() {
        binding.teacherFragmentSearch.clearFocus();
        binding.teacherFragmentSearch.setQuery(null, false);
        myCoursesBtnForParent = true;
        myPostedRequestsBtnForTeacher = false;
        browseParentPostedRequestsBtnForTeacher = false;
        binding.filterLayout.setVisibility(View.GONE);
        binding.myCoursesBtn.setBackgroundResource(R.drawable.rounded_button_active);
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        database.getAllTeacherCourses(email,this);
    }

    private void btnMyPostedRequestsClicked() {
        binding.teacherFragmentSearch.clearFocus();
        binding.teacherFragmentSearch.setQuery(null, false);
        myCoursesBtnForParent = false;
        myPostedRequestsBtnForTeacher = true;
        browseParentPostedRequestsBtnForTeacher = false;
        binding.filterLayout.setVisibility(View.GONE);
        binding.myCoursesBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_active);


        database.getTeacherPostedRequests(email, this);
    }

    private void searchInTeacherPostedRequestsForTeacher(String textToSearch) {
        if(!teacherPostedRequestsList.isEmpty()){
            List<TeacherPostRequest> filteredRequests = new ArrayList<>();
            for (TeacherPostRequest postRequest : teacherPostedRequestsList) {
                if (myPostedRequestsMatchesQuery(postRequest,textToSearch)) {
                    filteredRequests.add(postRequest);
                }
            }
            if(filteredRequests.isEmpty()){
                binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                binding.addedCoursesRecyclerView.setVisibility(View.GONE);
                binding.noDataAddedText.setVisibility(View.GONE);
            }
            else {
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
                binding.noDataAddedText.setVisibility(View.GONE);
                teacherPostedRequestsAdapter.filteredList(filteredRequests);
            }
        }

        else {
            binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            binding.noDataAddedText.setVisibility(View.GONE);
        }
    }

    private boolean myPostedRequestsMatchesQuery(TeacherPostRequest request, String query){
        query = query.toLowerCase();

        String requestDays = request.getAvailability().toLowerCase().trim();
        String fullAvail = "";

        if (!requestDays.isEmpty() && requestDays.charAt(requestDays.length() - 1) == ',') {
            requestDays = requestDays.substring(0, requestDays.length() - 1);
        }

        String [] splitAvail = requestDays.split(",");
        for(String str : splitAvail){
            if(str.toLowerCase().contains("sun")){
                fullAvail += "Sunday , ";
            }

            if(str.toLowerCase().contains("mon")){
                fullAvail += "Monday , ";
            }
            if(str.toLowerCase().contains("tues")){
                fullAvail += "Tuesday , ";
            }
            if(str.toLowerCase().contains("wed")){
                fullAvail += "Wednesday , ";
            }
            if(str.toLowerCase().contains("thur")){
                fullAvail += "Thursday , ";
            }
            if(str.toLowerCase().contains("fri")){
                fullAvail += "Friday";
            }
            if(str.toLowerCase().contains("sat")){
                fullAvail += "Saturday , ";
            }
        }



        if (fullAvail.toLowerCase().contains(query) ||
                request.getCourses().toLowerCase().contains(query) ||
                request.getLocation().toLowerCase().contains(query) ||
                request.getDuration().toLowerCase().contains(query) ||
                request.getTeachingMethod().toLowerCase().contains(query) ||
                request.getStartTime().toLowerCase().contains(query) ||
                request.getEndTime().toLowerCase().contains(query) ||
                String.valueOf(request.getPrice()).contains(query) ||
                request.getStartDate().toLowerCase().contains(query) ||
                request.getEndDate().toLowerCase().contains(query)) {
            return true;
        }



        Teacher teacher = request.getTeacherData();
        if(teacher != null &&(teacher.getEmail().trim().equalsIgnoreCase(query) ||
                teacher.getCollege().toLowerCase().contains(query) ||
                teacher.getField().toLowerCase().contains(query) ||
                (teacher.getGender() == 0 ? "female" : "male").contains(query) ||
                teacher.getAvailability().toLowerCase().contains(query) ||
                teacher.getEducationalLevel().toLowerCase().contains(query) ||
                teacher.getTeacherName().toLowerCase().contains(query))){
            return true;
        }

        if (teacher != null) {
            for (String phoneNumber : teacher.getPhoneNumbersList()) {
                if (phoneNumber.contains(query)) {
                    return true;
                }
            }
            for (Address address : teacher.getAddressesList()) {
                if (address.getCity().toLowerCase().contains(query) || address.getCountry().toLowerCase().contains(query)) {
                    return true;
                }
            }
        }

        return false;
    }




 /*   private void setTeacherPostedData() {
        binding.myCoursesBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_active);
        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInParentPostedRequestsForTeacher(newText);
                return true;
            }
        });
        database.getTeacherPostedRequests(email, this);
    }*/


    private void updatePostedAdapterData() {
        if (teacherPostedRequestsList.isEmpty()) {
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.refreshRecyclerView.setRefreshing(false);
            binding.refreshRecyclerView.setEnabled(false);
        } else {
            teacherPostedRequestsAdapter = new TeacherPostedRequestsAdapter(teacherPostedRequestsList, getContext(), this);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.refreshRecyclerView.setEnabled(true);
            binding.refreshRecyclerView.setRefreshing(false);
            binding.addedCoursesRecyclerView.setAdapter(teacherPostedRequestsAdapter);
        }

        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInTeacherPostedRequestsForTeacher(newText);
                return true;
            }
        });


        if(getView() != null)
            Snackbar.make(getView(), "Your Posted Requests Updated", Snackbar.LENGTH_SHORT).setDuration(500).show();
    }

    private void getTeacherDataFromActivity() {
        if (getArguments() != null) {
            email = getArguments().getString("email");
            teacherName = getArguments().getString("firstName");
            teacherName = teacherName + " " + getArguments().getString("lastName");
            teacherAvailability = getArguments().getString("availability");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                parentPostedRequestsForTeacherList = getArguments().getParcelableArrayList("teacherMatchingData", TeacherMatchModel.class);
            } else {
                parentPostedRequestsForTeacherList = getArguments().getParcelableArrayList("teacherMatchingData");
            }
        } else {
            email = "";
            teacherName = "";
            teacherAvailability = "";
        }
    }



    public void setMyCoursesAdapter() {
        if(!teacherCoursesList.isEmpty()){
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
            teacherCoursesAdapter = new TeacherCoursesAdapter(teacherCoursesList,getContext(),this);
            binding.addedCoursesRecyclerView.setAdapter(teacherCoursesAdapter);
        }
        else {
            binding.noDataAddedText.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
        }

        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInTeacherCourses(newText);
                return true;
            }
        });
        if(getView() != null)
            Snackbar.make(getView(), "Your Courses List Updated", Snackbar.LENGTH_SHORT).setDuration(500).show();
        binding.refreshRecyclerView.setRefreshing(false);
    }



    private void searchInTeacherCourses(String textToSearch){
        if(!teacherCoursesList.isEmpty()){
            List<Course> filteredCourses = new ArrayList<>();
            for (Course course : teacherCoursesList) {
                if (myCoursesMatchesQuery(course, textToSearch)) {
                    filteredCourses.add(course);
                }
            }
            if(filteredCourses.isEmpty()){
                binding.noDataAddedText.setVisibility(View.VISIBLE);
                binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            }
            else {
                binding.noDataAddedText.setVisibility(View.GONE);
                binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
                teacherCoursesAdapter.filter(filteredCourses);
            }
        }
        else {
            binding.noDataAddedText.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
        }
    }

    private boolean myCoursesMatchesQuery(Course course,String query){
        query = query.toLowerCase();


        String requestDays = course.getDays().toLowerCase().trim();
        String fullAvail = "";

        if (!requestDays.isEmpty() && requestDays.charAt(requestDays.length() - 1) == ',') {
            requestDays = requestDays.substring(0, requestDays.length() - 1);
        }

        String [] splitAvail = requestDays.split(",");
        for(String str : splitAvail){
            if(str.toLowerCase().contains("sun")){
                fullAvail += "Sunday , ";
            }

            if(str.toLowerCase().contains("mon")){
                fullAvail += "Monday , ";
            }
            if(str.toLowerCase().contains("tues")){
                fullAvail += "Tuesday , ";
            }
            if(str.toLowerCase().contains("wed")){
                fullAvail += "Wednesday , ";
            }
            if(str.toLowerCase().contains("thur")){
                fullAvail += "Thursday , ";
            }
            if(str.toLowerCase().contains("fri")){
                fullAvail += "Friday";
            }
            if(str.toLowerCase().contains("sat")){
                fullAvail += "Saturday , ";
            }
        }

        if (course.getTeacherEmail().toLowerCase().contains(query) ||
                course.getParentEmail().toLowerCase().contains(query) ||
                String.valueOf(course.getParentSentRequestId()).contains(query) ||
                String.valueOf(course.getTeacherSentRequestId()).contains(query) ||
                String.valueOf(course.getChildId()).contains(query) ||
                String.valueOf(course.getDuration()).contains(query) ||
                course.getEducationLevel().toLowerCase().contains(query) ||
                course.getCourses().toLowerCase().contains(query) ||
                fullAvail.toLowerCase().contains(query) ||
                course.getLocation().toLowerCase().contains(query) ||
                course.getTeachingMethod().toLowerCase().contains(query) ||
                course.getStartTime().toLowerCase().contains(query) ||
                course.getEndTime().toLowerCase().contains(query) ||
                course.getStartDate().toLowerCase().contains(query) ||
                course.getEndDate().toLowerCase().contains(query) ||
                String.valueOf(course.getPrice()).contains(query)) {
            return true;
        }

        Children child = course.getChild();
        if (child != null && (child.getChildName().toLowerCase().contains(query) ||
                String.valueOf(child.getChildAge()).contains(query) ||
                (child.getChildGender() == 0 ? "female" : "male").contains(query) ||
                convertGradeFormat(child.getGrade()).toLowerCase().contains(query))) {
            return true;
        }


        Parent parent = course.getParent();
        if (parent != null && (parent.getFirstName().toLowerCase().contains(query) ||
                parent.getLastName().toLowerCase().contains(query) ||
                parent.getEmail().toLowerCase().contains(query))) {
            return true;
        }


        if (parent != null) {
            for (String phoneNumber : parent.getPhoneNumbersList()) {
                if (phoneNumber.contains(query)) {
                    return true;
                }
            }
            for (Address address : parent.getAddressList()) {
                if (address.getCity().toLowerCase().contains(query) || address.getCountry().toLowerCase().contains(query)) {
                    return true;
                }
            }
        }

        Teacher teacher = course.getTeacher();
        if (teacher != null && (teacher.getTeacherName().toLowerCase().contains(query) ||
                teacher.getEmail().toLowerCase().contains(query))) {
            return true;
        }

        if (teacher != null) {
            for (String phoneNumber : teacher.getPhoneNumbersList()) {
                if (phoneNumber.contains(query)) {
                    return true;
                }
            }
            for (Address address : teacher.getAddressesList()) {
                if (address.getCity().toLowerCase().contains(query) || address.getCountry().toLowerCase().contains(query)) {
                    return true;
                }
            }
        }

        return false ;
    }

    private String convertGradeFormat(int grade) {
        if (grade >= 11 && grade <= 13) {
            return grade + "th grade";
        }
        switch (grade % 10) {
            case 1:
                return grade + "st grade";
            case 2:
                return grade + "nd grade";
            case 3:
                return grade + "rd grade";
            default:
                return grade + "th grade";
        }
    }

    private void setAvailableTeacherMatchingAdapter() {
        if (parentPostedRequestsForTeacherList.isEmpty()) {
            binding.noDataAddedText.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noMatchedData.setVisibility(View.GONE);

        } else {
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noMatchedData.setVisibility(View.GONE);
            matchingTeacherAdapter = new MatchingTeacherAdapter(parentPostedRequestsForTeacherList, getContext(), this);
            binding.addedCoursesRecyclerView.setAdapter(matchingTeacherAdapter);
        }
        binding.myCoursesBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_active);


        binding.teacherFragmentSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInParentPostedRequestsForTeacher(newText);
                return true;
            }
        });
        if(getView() != null)
            Snackbar.make(getView(), "Parent Posted Requests Updated", Snackbar.LENGTH_SHORT).setDuration(500).show();
        myCoursesBtnForParent = false;
        myPostedRequestsBtnForTeacher = false;
        updateBtnStatus();
    }

    private void updateBtnStatus() {
        binding.myCoursesBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
        binding.myPostedRequestsBtn.setBackgroundResource(R.drawable.rounded_button_inactive);
    }


    private void searchInParentPostedRequestsForTeacher(String str) {
        if(!parentPostedRequestsForTeacherList.isEmpty()){
            List<TeacherMatchModel> filteredRequests = new ArrayList<>();
            for (TeacherMatchModel matchModel : parentPostedRequestsForTeacherList) {
                if (parentPostedRequestsMatchesQuery(matchModel,str)) {
                    filteredRequests.add(matchModel);
                }
            }
            if(filteredRequests.isEmpty()){
                binding.noMatchedData.setVisibility(View.VISIBLE);
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.noDataAddedText.setVisibility(View.GONE);
                binding.addedCoursesRecyclerView.setVisibility(View.GONE);
            }
            else {
                binding.noMatchedData.setVisibility(View.GONE);
                binding.noPostedRequestTextView.setVisibility(View.GONE);
                binding.noDataAddedText.setVisibility(View.GONE);
                binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
                matchingTeacherAdapter.filteredList(filteredRequests);
            }
        }
        else {
            binding.noMatchedData.setVisibility(View.VISIBLE);
            binding.noPostedRequestTextView.setVisibility(View.GONE);
            binding.noDataAddedText.setVisibility(View.GONE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
        }
    }

    private boolean parentPostedRequestsMatchesQuery(TeacherMatchModel model ,String query){
        query = query.toLowerCase();


        String requestDays = model.getChoseDays().toLowerCase().trim();
        String fullAvail = "";

        if (!requestDays.isEmpty() && requestDays.charAt(requestDays.length() - 1) == ',') {
            requestDays = requestDays.substring(0, requestDays.length() - 1);
        }

        String [] splitAvail = requestDays.split(",");
        for(String str : splitAvail){
            if(str.toLowerCase().contains("sun")){
                fullAvail += "Sunday , ";
            }

            if(str.toLowerCase().contains("mon")){
                fullAvail += "Monday , ";
            }
            if(str.toLowerCase().contains("tues")){
                fullAvail += "Tuesday , ";
            }
            if(str.toLowerCase().contains("wed")){
                fullAvail += "Wednesday , ";
            }
            if(str.toLowerCase().contains("thur")){
                fullAvail += "Thursday , ";
            }
            if(str.toLowerCase().contains("fri")){
                fullAvail += "Friday";
            }
            if(str.toLowerCase().contains("sat")){
                fullAvail += "Saturday , ";
            }
        }


        if(model != null && (model.getParentEmail().toLowerCase().contains(query) ||
                fullAvail.toLowerCase().contains(query) ||
                model.getCourses().toLowerCase().contains(query) ||
                model.getLocation().toLowerCase().contains(query) ||
                model.getTeachingMethod().toLowerCase().contains(query)||
                model.getStartTime().contains(query) ||
                model.getStartDate().contains(query) ||
                model.getEndDate().contains(query) ||
                model.getPostDate().contains(query) ||
                String.valueOf(model.getPriceMinimum()).contains(query)||
                String.valueOf(model.getPriceMaximum()).contains(query))){
            return true;
        }

        CustomChildData customChildData = model.getCustomChildData();
        if(customChildData != null && (customChildData.getChildName().toLowerCase().contains(query) ||
                convertGradeFormat(customChildData.getChildGrade()).toLowerCase().contains(query) ||
                (customChildData.getGender() == 0 ? "Female" : "Male").contains(query) ||
                String.valueOf(customChildData.getChildAge()).contains(query))){
            return true;
        }
        return false;
    }

    private void initDatabase() {
        database = new Database(getContext());
    }

    private void initCoursesRecyclerView() {
        binding.addedCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onCardClicked(TeacherMatchModel teacherMatchModel) {
        tempTeacherMatchModel = teacherMatchModel;
        database.getParentInformation(teacherMatchModel.getParentEmail(), this);
    }

    private void sendRefreshBroadcast(int flag) {
        if (flag == 1) {
            database.getAllTeacherCourses(email,this);
        } else if (flag == 2) {
            database.getTeacherPostedRequests(email, this);
        } else if (flag == 3) {
            database.getTeacherMatchingData(email, this);
        }
        //Intent intent=new Intent();
        //intent.setAction("REFRESH_FRAGMENT_TEACHER");
        //LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
    }

    @Override
    public void onMatchingAdded(int resultFlag) {

    }

    @Override
    public void getTeacherMatchingData(int resultFlag, JSONArray teacherMatchingData) {
        if (resultFlag == 0) {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Error", "An Error occurred fetching the data , please try again later");
        } else if (resultFlag == -2) {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Network Error", "A Network Error occurred,Please try again later");
        } else if (resultFlag == -1) {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Request Error", "Something went wrong please try again later");
        } else if (resultFlag == 1) {
            try {
                if (!parentPostedRequestsForTeacherList.isEmpty()) {
                    parentPostedRequestsForTeacherList.clear();
                }
                for (int i = teacherMatchingData.length() - 1; i >= 0; i--) {
                    JSONObject jsonObject = teacherMatchingData.getJSONObject(i);
                    int matchingId = jsonObject.getInt("matchingId");
                    String parentEmail = jsonObject.getString("parentEmail");
                    int childId = jsonObject.getInt("childId");
                    String choseDays = jsonObject.getString("choseDays");
                    String choseCourses = jsonObject.getString("courses");
                    String location = jsonObject.getString("location");
                    String teachingMethod = jsonObject.getString("teachingMethod");
                    String childName = jsonObject.getString("childName");
                    String childAge = jsonObject.getString("childAge");
                    int childGender = jsonObject.getInt("childGender");
                    int childGrade = jsonObject.getInt("childGrade");
                    String startTime = jsonObject.getString("startTime");
                    String endTime = jsonObject.getString("endTime");
                    double priceMin = jsonObject.getDouble("priceMin");
                    double priceMax = jsonObject.getDouble("priceMax");
                    String startDate = jsonObject.getString("startDate");
                    String endDate = jsonObject.getString("endDate");
                    String postedDate = jsonObject.getString("posted");
                    TeacherMatchModel teacherMatchModel = new TeacherMatchModel(matchingId, parentEmail, new CustomChildData(childId, childName, childGrade, childGender, Integer.parseInt(childAge)),
                            choseDays, choseCourses, location, teachingMethod,
                            new Children(childName, childAge, childGender, childGrade), startTime, endTime, priceMin, priceMax, startDate, endDate,postedDate);
                    parentPostedRequestsForTeacherList.add(teacherMatchModel);
                }
                // matchingTeacherAdapter.filteredList(parentPostedRequestsForTeacherList);

                setAvailableTeacherMatchingAdapter();
                binding.refreshRecyclerView.setRefreshing(false);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateParentPostedRequestsForTeacher() {

    }


    @Override
    public void onDataFetched(int flag, JSONArray data) {
        if (flag == 1) {
            if (data.length() == 0) {
                binding.addedCoursesRecyclerView.setVisibility(View.GONE);
                binding.noDataAddedText.setVisibility(View.VISIBLE);
                binding.refreshRecyclerView.setRefreshing(false);
            } else {
                if (!teacherPostedRequestsList.isEmpty())
                    teacherPostedRequestsList.clear();
                try {
                    if (!teacherPhoneNumbersList.isEmpty()) {
                        teacherPhoneNumbersList.clear();
                    }
                    if (!teacherAddressesList.isEmpty())
                        teacherAddressesList.clear();

                    for (int i = (data.length() - 1); i >= 0; i--) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        String firstName = jsonObject.getString("firstname");
                        String lastname = jsonObject.getString("lastname");
                        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                        lastname = lastname.substring(0, 1).toUpperCase() + lastname.substring(1).toLowerCase();

                        int gender = jsonObject.getInt("gender");
                        int profileType = jsonObject.getInt("profileType");
                        int studentOrGraduate = jsonObject.getInt("studentOrGraduate");
                        String idNumber = jsonObject.getString("idNumber");
                        String expectedGraduationYear = jsonObject.getString("expectedGraduationYear");
                        String college = jsonObject.getString("college");
                        String field = jsonObject.getString("field");
                        String availability = jsonObject.getString("availabilityForJob");
                        int postId = jsonObject.getInt("postId");
                        String teacherEmail = jsonObject.getString("teacherEmail");
                        String courses = jsonObject.getString("courses");
                        String educationLevel = jsonObject.getString("educationLevel");
                        String duration = jsonObject.getString("duration");
                        String location = jsonObject.getString("location");
                        String teachingMethod = jsonObject.getString("teachingMethod");
                        String startTime = jsonObject.getString("startTime");
                        String endTime = jsonObject.getString("endTime");
                        String startDate = jsonObject.getString("startDate");
                        String endDate = jsonObject.getString("endDate");
                        double price = jsonObject.getDouble("price");
                        String postDate = jsonObject.getString("posted");
                        if (i == data.length() - 1) {
                            String phoneNumbers = jsonObject.getString("phoneNumbers");
                            if (phoneNumbers.contains(",")) {
                                String[] splitPhoneNumbers = phoneNumbers.split(",");
                                teacherPhoneNumbersList.addAll(Arrays.asList(splitPhoneNumbers));
                            } else
                                teacherPhoneNumbersList.add(phoneNumbers.trim());

                            String address = jsonObject.getString("addresses");
                            if (address.contains("|")) {
                                String[] splitAddress = address.split("\\|");
                                for (String str : splitAddress) {
                                    String[] splitCurrentAddress = str.split(",");
                                    teacherAddressesList.add(new Address(splitCurrentAddress[0].trim(), splitCurrentAddress[1].trim()));
                                }
                            } else {
                                String[] splitCurrentAddress = address.split(",");
                                teacherAddressesList.add(new Address(splitCurrentAddress[0].trim(), splitCurrentAddress[1].trim()));
                            }
                        }
                        teacherPostedRequestsList.add(new TeacherPostRequest(postId, email, courses, educationLevel, duration, availability, location,
                                teachingMethod, new Teacher(email, idNumber, studentOrGraduate + "",
                                expectedGraduationYear, college, field, gender, availability, educationLevel,
                                teacherAddressesList, teacherPhoneNumbersList, firstName + " " + lastname), startTime, endTime, price, startDate, endDate,postDate));
                    }
                    updatePostedAdapterData();
                    binding.refreshRecyclerView.setRefreshing(false);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {

        }
    }

    @Override
    public void onTeacherPostClicked(TeacherPostRequest teacherPostRequest) {
        binding.loadingProgressBar2.setVisibility(View.VISIBLE);
        if (getContext() != null && !isTeacherPostedRequestCardDialogShowing) {
            teacherPostedRequestCardDialog = new Dialog(getContext());
            teacherPostedRequestCardLayoutBinding = TeacherPostedRequestCardLayoutBinding.inflate(LayoutInflater.from(getContext()));
            teacherPostedRequestCardDialog.setContentView(teacherPostedRequestCardLayoutBinding.getRoot());
            teacherPostedRequestCardDialog.setCancelable(false);

// Get display metrics to calculate screen width
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;

            Window window = teacherPostedRequestCardDialog.getWindow();
            if (window != null) {
                window.setLayout((int) (screenWidth * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);

                // Optional: Set gravity to center
                window.setGravity(Gravity.CENTER);
            }

            isTeacherPostedRequestCardDialogShowing = true;
            new Handler().postDelayed(() -> {
                binding.loadingProgressBar2.setVisibility(View.GONE);
                teacherPostedRequestCardDialog.show();
            }, 300);
            teacherPostedRequestCardLayoutBinding.closeImageView.setOnClickListener(x -> {
                isTeacherPostedRequestCardDialogShowing=false;
                teacherPostedRequestCardDialog.dismiss();
            });

            teacherPostedRequestCardLayoutBinding.teacherNameTextView.setText(teacherPostRequest.getTeacherData().getTeacherName());
            teacherPostedRequestCardLayoutBinding.teacherEmailTextView.setText(teacherPostRequest.getTeacherEmail());
            List<String> phones = teacherPostRequest.getTeacherData().getPhoneNumbersList();
            StringBuilder phonesStringBuilder = new StringBuilder();
            for (int i = 0; i < phones.size(); i++) {
                if (i + 1 != phones.size())
                    phonesStringBuilder.append(phones.get(i)).append("\n");
                else
                    phonesStringBuilder.append(phones.get(i));
            }
            teacherPostedRequestCardLayoutBinding.teacherPhoneNumberTextView.setText(phonesStringBuilder.toString());
            teacherPostedRequestCardLayoutBinding.coursesTextView.setText(teacherPostRequest.getCourses());
            teacherPostedRequestCardLayoutBinding.teachingMethodTextView.setText(teacherPostRequest.getTeachingMethod());
            teacherPostedRequestCardLayoutBinding.timeTextView.setText(teacherPostRequest.getTeacherData().getAvailability());
            teacherPostedRequestCardLayoutBinding.locationTextView.setText(teacherPostRequest.getLocation());
            teacherPostedRequestCardLayoutBinding.priceTextView.setText(teacherPostRequest.getPrice() + "$");
            teacherPostedRequestCardLayoutBinding.dateTextView.setText(teacherPostRequest.getStartDate() + "  -  " + teacherPostRequest.getEndDate());
            if (teacherPostRequest.getDuration().equalsIgnoreCase("1"))
                teacherPostedRequestCardLayoutBinding.durationTextView.setText(teacherPostRequest.getDuration() + " Month");
            else
                teacherPostedRequestCardLayoutBinding.durationTextView.setText(teacherPostRequest.getDuration() + " Months");
            teacherPostedRequestCardLayoutBinding.cardSettings.setOnClickListener(z -> {
                showSettingsPopupMenu(teacherPostRequest);
            });
        }
    }

    private void showSettingsPopupMenu(TeacherPostRequest teacherPostRequest) {
        PopupMenu popupMenu = new PopupMenu(getContext(), teacherPostedRequestCardLayoutBinding.cardSettings);
        popupMenu.getMenuInflater().inflate(R.menu.menu_parent_posted_card_view, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.editCard) {
                updatePostedTeacherLookForAJobLayoutBinding = UpdatePostedTeacherLookForAJobLayoutBinding.inflate(LayoutInflater.from(getContext()));
                setTeacherEditPostedRequest(teacherPostRequest);
            } else if (item.getItemId() == R.id.deleteCard) {
                deleteTeacherPostedRequest(teacherPostRequest);
            }
            return true;
        });
    }

    private void deleteTeacherPostedRequest(TeacherPostRequest teacherPostRequest) {
        if (getContext() != null) {
            ConfirmDeleteDialogLayoutBinding confirmDeleteDialogLayoutBinding = ConfirmDeleteDialogLayoutBinding.inflate(LayoutInflater.from(getContext()));
            deleteRequestConfirmationDialog = new Dialog(getContext());
            deleteRequestConfirmationDialog.setContentView(confirmDeleteDialogLayoutBinding.getRoot());
            deleteRequestConfirmationDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(deleteRequestConfirmationDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 600;
            deleteRequestConfirmationDialog.getWindow().setAttributes(layoutParams);
            if (deleteRequestConfirmationDialog.getWindow() != null)
                deleteRequestConfirmationDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            deleteRequestConfirmationDialog.show();

            confirmDeleteDialogLayoutBinding.deleteBtn.setOnClickListener(x -> {
                deletePostedRequest(teacherPostRequest);
            });

            confirmDeleteDialogLayoutBinding.cancelBtn.setOnClickListener(c -> {
                deleteRequestConfirmationDialog.dismiss();
            });
        }
    }

    private void deletePostedRequest(TeacherPostRequest teacherPostRequest) {
        database.deleteTeacherPostedRequest(teacherPostRequest, this);
    }


    private void setTeacherEditPostedRequest(TeacherPostRequest teacherPostRequest) {
        if (getContext() != null) {
            updatePostedRequestDialog = new Dialog(getContext());
            updatePostedRequestDialog.setContentView(updatePostedTeacherLookForAJobLayoutBinding.getRoot());
            updatePostedRequestDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(updatePostedRequestDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 2300;
            updatePostedRequestDialog.getWindow().setAttributes(layoutParams);
            if (updatePostedRequestDialog.getWindow() != null)
                updatePostedRequestDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            updatePostedRequestDialog.show();

            updatePostedTeacherLookForAJobLayoutBinding.closeTheDialog.setOnClickListener(z -> {
                updatePostedRequestDialog.dismiss();
            });
            setFlexBoxSelectedCourses(teacherPostRequest.getCourses());
            updatePostedTeacherLookForAJobLayoutBinding.addCourseTeacherLooksForJob.setOnClickListener(z -> {
                String selectedCourse = updatePostedTeacherLookForAJobLayoutBinding.coursesSpinner.getSelectedItem().toString();
                if (!checkIfCourseAddedToList(selectedCourse)) {
                    coursesList.add(selectedCourse);
                    updateFlexBox();
                } else
                    MyAlertDialog.showWarningCourseAdded(getContext());
            });

            setEducationLevelSpinner(teacherPostRequest);
            setSelectedDays(teacherPostRequest);
            updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.setText(teacherPostRequest.getDuration());


            updatePostedTeacherLookForAJobLayoutBinding.startTimeEdtText.setText(teacherPostRequest.getStartTime());
            updatePostedTeacherLookForAJobLayoutBinding.endTimeEdtText.setText(teacherPostRequest.getEndTime());
            updatePostedTeacherLookForAJobLayoutBinding.priceEditText.setText(teacherPostRequest.getPrice() + "");
            updatePostedTeacherLookForAJobLayoutBinding.startDateEdtText.setText(teacherPostRequest.getStartDate());
            updatePostedTeacherLookForAJobLayoutBinding.endDateEdtText.setText(teacherPostRequest.getEndDate());
            startTime = teacherPostRequest.getStartTime();
            endTime = teacherPostRequest.getEndTime();

            updatePostedTeacherLookForAJobLayoutBinding.startTimeEdtText.setOnClickListener(x -> {
                setStartTime();
            });
            updatePostedTeacherLookForAJobLayoutBinding.endTimeEdtText.setOnClickListener(v -> {
                setEndTime();
            });

            updatePostedTeacherLookForAJobLayoutBinding.startDateEdtText.setOnClickListener(z -> {
                setStartDate();
            });
            updatePostedTeacherLookForAJobLayoutBinding.endDateEdtText.setOnClickListener(z -> {
                setEndDate();
            });



            String[] locationEntries = getResources().getStringArray(R.array.locationArray);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, locationEntries);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            updatePostedTeacherLookForAJobLayoutBinding.locationSpinner.setAdapter(adapter);
            setLocationSpinnerSelectedItem(locationEntries, teacherPostRequest);
            setTeachingMethodSpinner(teacherPostRequest);

            updatePostedTeacherLookForAJobLayoutBinding.updateTeacherRequestBtn.setOnClickListener(z -> {
                try {
                    updateBtnClicked(teacherPostRequest);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


    private void updateBtnClicked(TeacherPostRequest teacherPostRequest) throws ParseException {
        String startDate = updatePostedTeacherLookForAJobLayoutBinding.startDateEdtText.getText().toString();
        String endDate = updatePostedTeacherLookForAJobLayoutBinding.endDateEdtText.getText().toString();
        if (updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.getChildCount() == 0) {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Courses Error", "Please choose at least one course and add it ,,");
        } else {
            if (!updatePostedTeacherLookForAJobLayoutBinding.sunday.isChecked() && !updatePostedTeacherLookForAJobLayoutBinding.saturday.isChecked() &&
                    !updatePostedTeacherLookForAJobLayoutBinding.monday.isChecked() && !updatePostedTeacherLookForAJobLayoutBinding.tuesday.isChecked() &&
                    !updatePostedTeacherLookForAJobLayoutBinding.wednesday.isChecked() && !updatePostedTeacherLookForAJobLayoutBinding.thursday.isChecked() &&
                    !updatePostedTeacherLookForAJobLayoutBinding.friday.isChecked()) {
                MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "No days", "No days selected , please choose at least one day ..");
            } else {
                int numOfMonths = Integer.parseInt(updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.getText().toString());
                if (updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.getText().toString().isEmpty() || numOfMonths <= 0 || numOfMonths > 12) {
                    MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Wrong duration", "The duration must be at least one month and at most 12 months");
                } else {
                    if (!checkStartAndEndDate(Integer.parseInt(updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.getText().toString()))) {
                        MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Wrong Date", "Please choose a valid start and end dates in future with  " + (Integer.parseInt(updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.getText().toString())) + " months period");
                    } else {
                        if (!checkStartAndEndTime()) {
                            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Wrong timing", "Please Choose valid start and end time, and make sure there are at least one hour..");
                        } else {
                            double price = Double.parseDouble(updatePostedTeacherLookForAJobLayoutBinding.priceEditText.getText().toString());
                            if (updatePostedTeacherLookForAJobLayoutBinding.priceEditText.getText().toString().isEmpty() || price < 1.0 || price > 100.0)
                                MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Invalid Price", "Please Choose A Valid Price Value ..\n0.0 - 100 $");
                            else {
                                StringBuilder updatedCourses = new StringBuilder();
                                String updatedEducationLevel = updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.getSelectedItem().toString();
                                StringBuilder updatedDays = new StringBuilder();
                                String updatedNumOfMonths = updatePostedTeacherLookForAJobLayoutBinding.numberOfMonthsEdtText.getText().toString();
                                String updatedLocation = updatePostedTeacherLookForAJobLayoutBinding.locationSpinner.getSelectedItem().toString();
                                String updatedTeachingMethod = updatePostedTeacherLookForAJobLayoutBinding.teachingMethodSpinner.getSelectedItem().toString();
                                for (int i = 0; i < coursesList.size(); i++) {
                                    if (i + 1 != coursesList.size())
                                        updatedCourses.append(coursesList.get(i)).append(" , ");
                                    else
                                        updatedCourses.append(coursesList.get(i));
                                }
                                if (updatePostedTeacherLookForAJobLayoutBinding.saturday.isChecked())
                                    updatedDays.append("Sat , ");
                                if (updatePostedTeacherLookForAJobLayoutBinding.sunday.isChecked())
                                    updatedDays.append("Sun , ");
                                if (updatePostedTeacherLookForAJobLayoutBinding.monday.isChecked())
                                    updatedDays.append("Mon , ");
                                if (updatePostedTeacherLookForAJobLayoutBinding.tuesday.isChecked())
                                    updatedDays.append("Tues , ");
                                if (updatePostedTeacherLookForAJobLayoutBinding.wednesday.isChecked())
                                    updatedDays.append("Wed , ");
                                if (updatePostedTeacherLookForAJobLayoutBinding.thursday.isChecked())
                                    updatedDays.append("Thur , ");
                                if (updatePostedTeacherLookForAJobLayoutBinding.friday.isChecked())
                                    updatedDays.append("Fri");

                                TeacherPostRequest tpr = new TeacherPostRequest(teacherPostRequest.getTeacherPostRequestId(),
                                        teacherPostRequest.getTeacherEmail(), updatedCourses.toString(), updatedEducationLevel,
                                        updatedNumOfMonths, updatedDays.toString(), updatedLocation, updatedTeachingMethod, startTime, endTime, price, startDate, endDate);
                                binding.progressBarLayout.setVisibility(View.VISIBLE);
                                database.updateTeacherPostedRequest(tpr, this);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setEndDate() {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select End Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
            updatePostedTeacherLookForAJobLayoutBinding.endDateEdtText.setText(date);
        });
        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "");
    }

    private void setStartDate() {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Start Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
            updatePostedTeacherLookForAJobLayoutBinding.startDateEdtText.setText(date);
        });
        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "");
    }

    private boolean checkStartAndEndDate(int duration) {
        try {
            return areDatesValid(
                    updatePostedTeacherLookForAJobLayoutBinding.startDateEdtText.getText().toString(),
                    updatePostedTeacherLookForAJobLayoutBinding.endDateEdtText.getText().toString(),
                    duration
            );
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public boolean areDatesValid(String startDateStr, String endDateStr, int duration) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate   = LocalDate.parse(endDateStr, formatter);
        LocalDate today     = LocalDate.now();
        if (startDate.isBefore(today) || endDate.isBefore(today)) {
            return false;
        }
        if (!endDate.isAfter(startDate)) {
            return false;
        }
        long monthsDifference = ChronoUnit.MONTHS.between(startDate, endDate);
        return monthsDifference == duration;
    }


    private boolean checkStartAndEndTime() throws ParseException {
        Date startDate = timeFormat.parse(startTime);
        Date endDate = timeFormat.parse(endTime);

        if (startDate != null && endDate != null && startDate.before(endDate) && isEndTimeAtLeastOneHourLater(startDate, endDate)) {
            return true;
        }
        return false;
    }

    private boolean isEndTimeAtLeastOneHourLater(Date startTime, Date endTime) {
        final long oneHourInMillis = 3600000;
        Date oneHourLater = new Date(startTime.getTime() + oneHourInMillis);
        return endTime.after(oneHourLater);
    }

    private void setTeachingMethodSpinner(TeacherPostRequest teacherPostRequest) {
        if (teacherPostRequest.getTeachingMethod().equalsIgnoreCase("Face To Face"))
            updatePostedTeacherLookForAJobLayoutBinding.teachingMethodSpinner.setSelection(1);
        else
            updatePostedTeacherLookForAJobLayoutBinding.teachingMethodSpinner.setSelection(0);
    }

    private void setLocationSpinnerSelectedItem(String[] locationEntries, TeacherPostRequest teacherPostRequest) {
        for (int i = 0; i < locationEntries.length; i++) {
            if (locationEntries[i].equals(teacherPostRequest.getLocation())) {
                updatePostedTeacherLookForAJobLayoutBinding.locationSpinner.setSelection(i);
                break;
            }
        }
    }


    private void setStartTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.setTimeZone(TimeZone.getDefault());


                SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
                String amPm = amPmFormat.format(c.getTime());
                amPmStart = amPm;


                SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                String time = format.format(c.getTime());
                startTime = time;
                updatePostedTeacherLookForAJobLayoutBinding.startTimeEdtText.setText(startTime);
            }
        }, hours, mins, false);
        timePickerDialog.show();
    }

    private void setEndTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.setTimeZone(TimeZone.getDefault());

                SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
                String amPm = amPmFormat.format(c.getTime());
                amPmEnd = amPm;

                SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                String time = format.format(c.getTime());
                endTime = time;
                updatePostedTeacherLookForAJobLayoutBinding.endTimeEdtText.setText(endTime);
            }
        }, hours, mins, false);
        timePickerDialog.show();
    }


    private void setSelectedDays(TeacherPostRequest teacherPostRequest) {
        if (teacherPostRequest.getTeacherData().getAvailability().contains("Sat")) {
            updatePostedTeacherLookForAJobLayoutBinding.saturday.setChecked(true);
        }

        if (teacherPostRequest.getTeacherData().getAvailability().contains("Sun")) {
            updatePostedTeacherLookForAJobLayoutBinding.sunday.setChecked(true);
        }

        if (teacherPostRequest.getTeacherData().getAvailability().contains("Mon")) {
            updatePostedTeacherLookForAJobLayoutBinding.monday.setChecked(true);
        }

        if (teacherPostRequest.getTeacherData().getAvailability().contains("Tues")) {
            updatePostedTeacherLookForAJobLayoutBinding.tuesday.setChecked(true);
        }

        if (teacherPostRequest.getTeacherData().getAvailability().contains("Wed")) {
            updatePostedTeacherLookForAJobLayoutBinding.wednesday.setChecked(true);
        }

        if (teacherPostRequest.getTeacherData().getAvailability().contains("Thur")) {
            updatePostedTeacherLookForAJobLayoutBinding.thursday.setChecked(true);
        }

        if (teacherPostRequest.getTeacherData().getAvailability().contains("Fri")) {
            updatePostedTeacherLookForAJobLayoutBinding.friday.setChecked(true);
        }
    }

    private void setEducationLevelSpinner(TeacherPostRequest teacherPostRequest) {
        if (teacherPostRequest.getEducationLevel().equals("Elementary School")) {
            updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.setSelection(0);
        } else if (teacherPostRequest.getEducationLevel().equals("Middle School")) {
            updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.setSelection(1);
        } else if (teacherPostRequest.getEducationLevel().equals("High School")) {
            updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.setSelection(2);
        } else {
            updatePostedTeacherLookForAJobLayoutBinding.educationalLevelSpinner.setSelection(3);
        }
    }

    private void updateFlexBox() {
        if (updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.getChildCount() > 0) {
            updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.removeAllViews();
        }
        for (String str : coursesList) {
            if (!checkIfCourseAddedToList(str))
                coursesList.add(str.trim());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.course_custom_card, updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout, false);
            TextView courseName = customView.findViewById(R.id.textViewCourseName);
            ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
            courseName.setText(str.trim());
            deleteImageView.setOnClickListener(e -> {
                updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.removeView(customView);
                for (int i = 0; i < coursesList.size(); i++) {
                    if (coursesList.get(i).equalsIgnoreCase(courseName.getText().toString())) {
                        coursesList.remove(coursesList.get(i));
                    }
                }
            });
            updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.addView(customView);
        }
    }

    private void setFlexBoxSelectedCourses(String courses) {
        String[] splittedCourses = courses.trim().split(",");
        try {
            for (String str : splittedCourses) {
                if (!checkIfCourseAddedToList(str))
                    coursesList.add(str.trim());
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View customView = inflater.inflate(R.layout.course_custom_card, updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout, false);
                TextView courseName = customView.findViewById(R.id.textViewCourseName);
                ImageView deleteImageView = customView.findViewById(R.id.imageViewDelete);
                courseName.setText(str.trim());
                deleteImageView.setOnClickListener(e -> {
                    updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.removeView(customView);
                    for (int i = 0; i < coursesList.size(); i++) {
                        if (coursesList.get(i).equalsIgnoreCase(courseName.getText().toString())) {
                            coursesList.remove(coursesList.get(i));
                        }
                    }
                });
                updatePostedTeacherLookForAJobLayoutBinding.coursesFlexBoxLayout.addView(customView);
            }
        } catch (Exception e) {
        }
    }

    private boolean checkIfCourseAddedToList(String selectedCourse) {
        for (String str : coursesList) {
            if (str.equalsIgnoreCase(selectedCourse)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTeacherPostRequestUpdate(int flag) {
        if (flag == -2) {

        } else if (flag == -1) {

        } else if (flag == 0) {

        } else if (flag == 1) {
            database.getTeacherPostedRequests(email, this);
            updatePostedRequestDialog.dismiss();
            isTeacherPostedRequestCardDialogShowing=false;
            teacherPostedRequestCardDialog.dismiss();
            new Handler().postDelayed(() -> {
                binding.progressBarLayout.setVisibility(View.GONE);
            }, 1500);
        } else {

        }
    }

    @Override
    public void onPostedRequestDeleted(int flag) {
        if (flag == 1) {
            database.getTeacherPostedRequests(email, this);
            deleteRequestConfirmationDialog.dismiss();
            isTeacherPostedRequestCardDialogShowing=false;
            teacherPostedRequestCardDialog.dismiss();
        } else if (flag == -1) {

        } else if (flag == -2) {

        } else if (flag == -0) {

        } else if (flag == -3) {

        }
    }

    @Override
    public void onResultParentInformation(int resultFlag, JSONArray parentInformation) {
        if (resultFlag == -1) {
            // error
        } else if (resultFlag == -2) {
            // No data
        } else if (resultFlag == -3) {
            // connection error
        } else if (resultFlag == -4) {
            // volley error
        } else {
            try {
                List<String> parentPhoneNumbersList = new ArrayList<>();
                List<Address> parentAddressesList = new ArrayList<>();

                for (int i = 0; i < parentInformation.length(); i++) {
                    JSONObject jsonObject = parentInformation.getJSONObject(i);
                    receiverEmail = jsonObject.getString("email");
                    parentFirstName = jsonObject.getString("firstname");
                    parentFirstName = parentFirstName.substring(0, 1).toUpperCase() + parentFirstName.substring(1).toLowerCase();
                    parentLastName = jsonObject.getString("lastname");
                    parentLastName = parentLastName.substring(0, 1).toUpperCase() + parentLastName.substring(1).toLowerCase();
                    String parentBirthDate = jsonObject.getString("birthDate");
                    String parentGender = "Male";
                    int parentGenderVal = jsonObject.getInt("gender");
                    if (parentGenderVal == 0)
                        parentGender = "Female";
                    String parentIdNumber = jsonObject.getString("idNumber");
                    if (i == 0) {
                        String parentPhoneNumbers = jsonObject.getString("phoneNumbers");
                        if (parentPhoneNumbers.contains(",")) {
                            String[] splitTeacherPhoneNumbers = parentPhoneNumbers.split(",");
                            parentPhoneNumbersList.addAll(Arrays.asList(splitTeacherPhoneNumbers));
                        } else
                            parentPhoneNumbersList.add(parentPhoneNumbers);
                        String parentAddresses = jsonObject.getString("addresses");
                        if (parentAddresses.contains("|")) {
                            String[] splitAddresses = parentAddresses.split("\\|");
                            for (String splitAddress : splitAddresses) {
                                String[] splitAddressObject = splitAddress.split(",");
                                parentAddressesList.add(new Address(splitAddressObject[0].trim(), splitAddressObject[1].trim()));
                            }
                        } else {
                            String[] split = parentAddresses.split(",");
                            parentAddressesList.add(new Address(split[0].trim(), split[1].trim()));
                        }
                    }
                }
                binding.loadingProgressBar2.setVisibility(View.VISIBLE);
                showTeacherMatchDialog(tempTeacherMatchModel, parentFirstName + " " + parentLastName, parentPhoneNumbersList);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showTeacherMatchDialog(TeacherMatchModel teacherMatchModel, String parentName, List<String> parentPhoneNumbers) {
        currentCourseToSendRequestMatchModel = teacherMatchModel;
        if (getContext() != null) {
            tempTeacherMatchModelForCheckTeacherSentRequest = teacherMatchModel;
            dialogTeacherMatchingOnCardClickedBinding = DialogTeacherMatchingOnCardClickedBinding.inflate(LayoutInflater.from(getContext()));
            parentPostedRequestsForTeacherDialog = new Dialog(getContext());
            parentPostedRequestsForTeacherDialog.setContentView(dialogTeacherMatchingOnCardClickedBinding.getRoot());
            parentPostedRequestsForTeacherDialog.setCancelable(false);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            parentPostedRequestsForTeacherDialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(parentPostedRequestsForTeacherDialog.getWindow().getAttributes());

            layoutParams.width = (int) (screenWidth * 0.90);
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            parentPostedRequestsForTeacherDialog.getWindow().setAttributes(layoutParams);

            // Set transparent background
            if (parentPostedRequestsForTeacherDialog.getWindow() != null) {
                parentPostedRequestsForTeacherDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                // Optional: Add window animations
                //parentPostedRequestsForTeacherDialog.getWindow().setWindowAnimations(R.style.);
            }

            tempRequestToSendDateTimeModel = new DateTimeModel(teacherMatchModel.getStartDate(),
                    teacherMatchModel.getEndDate(),
                    teacherMatchModel.getStartTime(),
                    teacherMatchModel.getEndTime(),
                    teacherMatchModel.getChoseDays());

            database.checkIfTeacherRequestSentBefore(email, currentCourseToSendRequestMatchModel, this);

            dialogTeacherMatchingOnCardClickedBinding.closeImageView.setOnClickListener(z -> {
                parentPostedRequestsForTeacherDialog.dismiss();
            });

            dialogTeacherMatchingOnCardClickedBinding.requestSentImageView.setOnClickListener(v -> {
                database.deleteTeacherSentRequestToParent(email, tempTeacherMatchModelForCheckTeacherSentRequest, this);
            });

            dialogTeacherMatchingOnCardClickedBinding.sendMessageToParentBtn.setOnClickListener(a->{
                getUserInfoByEmail(receiverEmail);
            });

            dialogTeacherMatchingOnCardClickedBinding.requestSentTextView.setOnClickListener(v -> {
                database.deleteTeacherSentRequestToParent(email, tempTeacherMatchModelForCheckTeacherSentRequest, this);
            });

            setDataToMatchDialog(teacherMatchModel, parentName, parentPhoneNumbers);
        }
    }


    private void getUserInfoByEmail(String tempMail) {
        DatabaseReference receiverUser = FirebaseDatabase.getInstance()
                .getReference("users");

        Query query = receiverUser
                .orderByChild("mail")
                .equalTo(receiverEmail);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    MyAlertDialog.warning(getContext(), "No Parent", "Cannot find parent for messaging, please try again later");
                    return;
                }
                for(DataSnapshot user: snapshot.getChildren()){
                    String userId = user.getKey();
                    String userName = user.child("userName").getValue(String.class);
                    String teacherEmail = user.child("mail").getValue(String.class);
                    String userImage = user.child("profilePic").getValue(String.class);
                   /* if(targetUser == null) {
                        MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Error", "Parent Error..");
                        return;
                    }*/
                    // Start Chatting here
                    startChatWindowActivity(userId, userName, userImage, teacherEmail);
                    break;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "No User", "Cannot Find Parent, please call the phone number or try again later");
                return;
            }
        });

        /*DatabaseReference mDatabase =
                FirebaseDatabase.getInstance().getReference("users");

        mDatabase.orderByChild("mail")
                .equalTo(tempMail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Log.d("USER_SEARCH", "No user found with this email");
                            MyAlertDialog.showCustomAlertDialogSpinnerError(
                                    getContext(),
                                    "Cannot Find Parent",
                                    "Cannot find the parent for messaging now, please try again later."
                            );
                            return;
                        }

                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            Users user = userSnapshot.getValue(Users.class);
                            if (user == null) return;

                            Log.d("USER_ID", user.getUserId());
                            Log.d("USER_NAME", user.getUserName());

                            // Launch chat window with the retrieved user
                            startChatWindowActivity(user);
                            break; // stop after first match
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FIREBASE_ERROR",
                                "Code: " + error.getCode() +
                                        " | Message: " + error.getMessage(),
                                error.toException());
                    }
                });*/

        /*DatabaseReference mDatabase =
                FirebaseDatabase.getInstance().getReference("users");

        mDatabase.orderByChild("email")
                .equalTo(tempMail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Log.d("USER_SEARCH", "No user found with this email");
                            MyAlertDialog.showCustomAlertDialogSpinnerError(getContext(), "Cannot Find Parent", "Cannot find the parent for messaging now, please try again later.");
                            return;
                        }
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            Users user = userSnapshot.getValue(Users.class);
                            if (user == null) return;
                            Log.d("USER_ID", user.getUserId());
                            Log.d("USER_NAME", user.getUserName());
                            startChatWindowActivity(user);
                            break;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FIREBASE_ERROR",
                                "Code: " + error.getCode() +
                                        " | Message: " + error.getMessage(),
                                error.toException());
                    }
                });*/
    }



    private void startChatWindowActivity(/*Users user*/String userId, String userName, String profilePic, String teacherEmail) {
        Intent intent = new Intent(getActivity(), ChatWindowActivity.class);
        intent.putExtra("nameeee",userName);
        intent.putExtra("email",teacherEmail);
        intent.putExtra("reciverImg",profilePic);
        intent.putExtra("uid",userId);
        startActivity(intent);
    }

    private void setDataToMatchDialog(TeacherMatchModel teacherMatchModel, String parentName, List<String> parentPhoneNumbers) {
        dialogTeacherMatchingOnCardClickedBinding.childNameTextView.setText(teacherMatchModel.getChildren().getChildName());
        dialogTeacherMatchingOnCardClickedBinding.parentNameTextView.setText(parentName);
        dialogTeacherMatchingOnCardClickedBinding.parentEmailTextView.setText(teacherMatchModel.getParentEmail());
        StringBuilder phoneNumbers = new StringBuilder();
        if (parentPhoneNumbers.size() > 1) {
            for (int i = 0; i < parentPhoneNumbers.size(); i++) {
                if (i + 1 != parentPhoneNumbers.size())
                    phoneNumbers.append(parentPhoneNumbers.get(i)).append(" , ");
                else
                    phoneNumbers.append(parentPhoneNumbers.get(i));
            }
        } else
            phoneNumbers.append(parentPhoneNumbers.get(0));
        dialogTeacherMatchingOnCardClickedBinding.parentPhoneNumberTextView.setText(phoneNumbers.toString());
        dialogTeacherMatchingOnCardClickedBinding.coursesTextView.setText(teacherMatchModel.getCourses());
        dialogTeacherMatchingOnCardClickedBinding.choseDaysTextView.setText(teacherMatchModel.getChoseDays());
        dialogTeacherMatchingOnCardClickedBinding.teachingMethodTextView.setText(teacherMatchModel.getTeachingMethod());
        dialogTeacherMatchingOnCardClickedBinding.timeTextView.setText(teacherMatchModel.getStartTime() + " - " + teacherMatchModel.getEndTime());
        dialogTeacherMatchingOnCardClickedBinding.locationTextView.setText(teacherMatchModel.getLocation());
        dialogTeacherMatchingOnCardClickedBinding.dateTextView.setText(teacherMatchModel.getStartDate() + "  -  " + teacherMatchModel.getEndDate());
        dialogTeacherMatchingOnCardClickedBinding.childNameTextView.setText(teacherMatchModel.getChildren().getChildName());
        if (teacherMatchModel.getCustomChildData().getChildGrade() == 1) {
            dialogTeacherMatchingOnCardClickedBinding.childGradeTextView.setText(teacherMatchModel.getCustomChildData().getChildGrade() + "st Grade");
        } else if (teacherMatchModel.getCustomChildData().getChildGrade() == 2) {
            dialogTeacherMatchingOnCardClickedBinding.childGradeTextView.setText(teacherMatchModel.getCustomChildData().getChildGrade() + "nd Grade");
        } else {
            dialogTeacherMatchingOnCardClickedBinding.childGradeTextView.setText(teacherMatchModel.getCustomChildData().getChildGrade() + "th Grade");
        }
        String childGender = "Male";
        if (teacherMatchModel.getCustomChildData().getGender() == 0) {
            childGender = "Female";
        }
        dialogTeacherMatchingOnCardClickedBinding.childGenderTextView.setText(childGender);
        if (teacherMatchModel.getCustomChildData().getChildAge() == 1) {
            dialogTeacherMatchingOnCardClickedBinding.childAgeTextView.setText(teacherMatchModel.getChildren().getChildAge() + " Year");
        } else {
            dialogTeacherMatchingOnCardClickedBinding.childAgeTextView.setText(teacherMatchModel.getChildren().getChildAge() + " Years");
        }
    }

    @Override
    public void onRequestsReceived(int flag, JSONArray requestsData) {
        if (flag == -2) {

        } else if (flag == -1) {

        } else if (flag == 0) {
            if (teacherReceivedRequestDialog != null && teacherReceivedRequestDialog.isShowing()) {
                teacherReceivedRequestDialog.dismiss();
            }
            setTeacherReceivedRequestsToRequestsDialog(null);
            teacherReceivedRequestsDialogLayoutBinding.refreshRecyclerView.setRefreshing(false);
        } else if (flag == 1) {
            if (requestsData != null) {
                try {
                    List<TeacherReceivedRequest> teacherReceivedRequestsList = new ArrayList<>();
                    for (int i = requestsData.length() - 1; i >= 0; i--) {
                        List<Children> childrenList = new ArrayList<>();
                        List<String> parentPhoneList = new ArrayList<>();
                        JSONObject jsonObject = requestsData.getJSONObject(i);
                        String parentFirstName = jsonObject.getString("firstName");
                        parentFirstName = parentFirstName.substring(0, 1).toUpperCase() + parentFirstName.substring(1).toLowerCase();
                        String parentLastName = jsonObject.getString("lastName");
                        parentLastName = parentLastName.substring(0, 1).toUpperCase() + parentLastName.substring(1).toLowerCase();
                        int parentRequestId = jsonObject.getInt("requestId");
                        int teacherPostRequestId = jsonObject.getInt("postId");
                        String parentEmail = jsonObject.getString("parentEmail");
                        String teacherEmail = jsonObject.getString("teacherEmail");
                        String educationLevel = jsonObject.getString("educationLevel");
                        String requestDateAndTime = jsonObject.getString("requestDate");
                        String requestDate = requestDateAndTime.split(" ")[0];
                        String requestTime = requestDateAndTime.split(" ")[1];
                        int isAccepted = jsonObject.getInt("isAccepted");
                        JSONArray children = jsonObject.getJSONArray("children");
                        for (int j = children.length() - 1; j >= 0; j--) {
                            JSONObject childObject = children.getJSONObject(j);
                            int childId = childObject.getInt("childId");
                            String childName = childObject.getString("childName");
                            String childGrade = childObject.getString("childGrade");
                            int childAge = childObject.getInt("childAge");
                            int childGender = childObject.getInt("childGender");
                            int childRequestId = childObject.getInt("childRequestId");
                            childrenList.add(new Children(childId, childName, childAge + "", childGender, Integer.parseInt(childGrade), childRequestId));
                        }
                        String courses = jsonObject.getString("courses");
                        String availabilityForJob = jsonObject.getString("availabilityForJob");
                        String duration = jsonObject.getString("duration");
                        String startTime = jsonObject.getString("startTime");
                        String endTime = jsonObject.getString("endTime");
                        String startDate = jsonObject.getString("startDate");
                        String endDate = jsonObject.getString("endDate");
                        double price = jsonObject.getDouble("price");
                        String location = jsonObject.getString("location");
                        String teachingMethod = jsonObject.getString("teachingMethod");
                        String parentPhoneNumbersStr = jsonObject.getString("parentPhoneNumbers");
                        String[] splitPhone = parentPhoneNumbersStr.split(",");
                        for (String phone : splitPhone) {
                            parentPhoneList.add(phone.trim());
                        }
                        teacherReceivedRequestsList.add(new TeacherReceivedRequest(parentRequestId, new TeacherPostRequest(teacherPostRequestId, teacherEmail, courses, educationLevel, duration,
                                availabilityForJob, location, startTime, endTime, startDate, endDate, price, teachingMethod),
                                new Parent(parentEmail,parentFirstName,parentLastName, parentPhoneList,0),
                                childrenList, isAccepted, requestDate, requestTime));

                    }
                    if (teacherReceivedRequestDialog != null && teacherReceivedRequestDialog.isShowing()) {
                        teacherReceivedRequestDialog.dismiss();
                    }
                    setTeacherReceivedRequestsToRequestsDialog(teacherReceivedRequestsList);
                    teacherReceivedRequestsDialogLayoutBinding.refreshRecyclerView.setRefreshing(false);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        } else {

        }
    }

    private void setTeacherReceivedRequestsToRequestsDialog(List<TeacherReceivedRequest> teacherReceivedRequests) {
        if (getContext() != null) {
            tempTeacherReceivedRequestsList = teacherReceivedRequests;

            teacherReceivedRequestDialog = new Dialog(requireContext());
            teacherReceivedRequestsDialogLayoutBinding =
                    TeacherReceivedRequestsDialogLayoutBinding.inflate(LayoutInflater.from(requireContext()));

            teacherReceivedRequestDialog.setContentView(teacherReceivedRequestsDialogLayoutBinding.getRoot());
            teacherReceivedRequestDialog.setCancelable(false);

            Window window = teacherReceivedRequestDialog.getWindow();
            if (window != null) {

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());

                DisplayMetrics displayMetrics = new DisplayMetrics();
                window.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                layoutParams.width = (int) (screenWidth * 0.95);
                layoutParams.height = (int) (screenHeight * 0.90);

                window.setAttributes(layoutParams);

                window.setBackgroundDrawableResource(android.R.color.transparent);
            }

            teacherReceivedRequestDialog.show();

            teacherReceivedRequestsDialogLayoutBinding.closeImage.setOnClickListener(z -> {
                teacherReceivedRequestDialog.dismiss();
            });

            teacherReceivedRequestsDialogLayoutBinding.refreshRecyclerView.setOnRefreshListener(() ->
                    database.getTeacherReceivedRequests(email, TeacherFragment.this));

            if (teacherReceivedRequests != null && !teacherReceivedRequests.isEmpty()) {
                teacherReceivedRequestsDialogLayoutBinding.noReceivedRequestsForTeacher.setVisibility(View.GONE);
                teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setVisibility(View.VISIBLE);

                teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                teacherReceivedRequestAdapter = new TeacherReceivedRequestAdapter(teacherReceivedRequests, this);
                teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setAdapter(teacherReceivedRequestAdapter);

            } else {
                teacherReceivedRequestsDialogLayoutBinding.noReceivedRequestsForTeacher.setVisibility(View.VISIBLE);
                teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onAcceptDeclineClicked(int flag, TeacherReceivedRequest teacherReceivedRequest) {
        tempTeacherReceivedRequestObject = teacherReceivedRequest;
        if (flag == 1) {
            tempTeacherPostId = teacherReceivedRequest.getParentRequestId();
            tempRequestId = teacherReceivedRequest.getParentRequestId();
            String availability = teacherReceivedRequest.getTeacherPostRequest().getAvailability().trim();
            if (availability.equalsIgnoreCase("Weekend")) {
                teacherReceivedRequest.getTeacherPostRequest().setAvailability("Thur , Fri");
            } else if (availability.equalsIgnoreCase("Any")) {
                teacherReceivedRequest.getTeacherPostRequest().setAvailability("Sat , Sun , Mon , Tues , Thur , Fri");
            }
            availability = teacherReceivedRequest.getTeacherPostRequest().getAvailability().trim();
            if (!availability.isEmpty() && availability.charAt(availability.length() - 1) == ',') {
                availability = (availability.substring(0, availability.length() - 1)).trim();
                teacherReceivedRequest.getTeacherPostRequest().setAvailability(availability);
            }

            currentRequestDate = new DateTimeModel(teacherReceivedRequest.getTeacherPostRequest().getStartDate(),
                    teacherReceivedRequest.getTeacherPostRequest().getEndDate(),
                    teacherReceivedRequest.getTeacherPostRequest().getStartTime(),
                    teacherReceivedRequest.getTeacherPostRequest().getEndTime(),
                    teacherReceivedRequest.getTeacherPostRequest().getAvailability());
            database.getAllTeacherCoursesDates(email,this);
          //  database.getAllTeacherCoursesDates(email, this);
        } else {
            database.setTeacherReceivedRequestToDecline(teacherReceivedRequest.getParentRequestId());
            int position = tempTeacherReceivedRequestsList.indexOf(teacherReceivedRequest);
            tempTeacherReceivedRequestsList.remove(tempTeacherReceivedRequestObject);
            teacherReceivedRequestAdapter.notifyItemRemoved(position);
            teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            if(getView() != null)
                Snackbar.make(getView(), "Request Declined ..", Snackbar.LENGTH_SHORT).setDuration(1500).show();
            if (tempTeacherReceivedRequestsList.isEmpty()) {
                teacherReceivedRequestsDialogLayoutBinding.noReceivedRequestsForTeacher.setVisibility(View.VISIBLE);
                teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setVisibility(View.GONE);
            } else {
                teacherReceivedRequestsDialogLayoutBinding.noReceivedRequestsForTeacher.setVisibility(View.GONE);
                teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onCoursesReceived(int flag, JSONArray coursesInformation) {

        if (flag == 1) {
            try {
                if (coursesInformation != null) {
                    int conflictFlag = 0;
                    for (int i = 0; i < coursesInformation.length(); i++) {
                        JSONObject jsonObject = coursesInformation.getJSONObject(i);
                        int teacherCourseId = jsonObject.getInt("courseId");
                        int teacherRequestId = jsonObject.getInt("parentSentRequestId");
                        String startDate = jsonObject.getString("startDate");
                        String endDate = jsonObject.getString("endDate");
                        String startTime = jsonObject.getString("startTime");
                        String endTime = jsonObject.getString("endTime");
                        String availabilityForJob = (jsonObject.getString("availabilityForJob")).trim();
                        String days = availabilityForJob;
                        if (availabilityForJob.equalsIgnoreCase("Weekend")) {
                            days = "Thur , Fri";
                        } else if (availabilityForJob.equalsIgnoreCase("Any")) {
                            days = "Sat , Sun , Mon , Tues , Thur , Fri";
                        }
                        if (availabilityForJob.charAt(availabilityForJob.length() - 1) == ',') {
                            days = availabilityForJob.substring(0, availabilityForJob.length() - 1).trim();
                        }

                        if (teacherRequestId != tempRequestId) {
                            if (DateUtils.isConflict(currentRequestDate, new DateTimeModel(startDate, endDate, startTime, endTime, days))) {
                                MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Course Conflict", "This Request Make A conflict with one of your existing courses");
                                conflictFlag = 1;
                                break;
                            }
                        }
                    }
                    if (conflictFlag != 1) {
                        // database.insertTeacherCourse(email,tempRequestId,tempTeacherPostId,this);
                        database.insertTeacherCourse(new TeacherPostRequest(email, tempRequestId,
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getCourses(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getEducationLevel(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getDuration(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getAvailability(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getLocation(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getTeachingMethod(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getStartDate(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getEndDate(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getStartTime(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getEndTime(),
                                tempTeacherReceivedRequestObject.getTeacherPostRequest().getPrice()),tempTeacherReceivedRequestObject.getParent().getEmail(),tempTeacherReceivedRequestObject.getChildren().get(0).getChildId(),this);
                        int position = tempTeacherReceivedRequestsList.indexOf(tempTeacherReceivedRequestObject);
                        tempTeacherReceivedRequestsList.remove(tempTeacherReceivedRequestObject);
                        teacherReceivedRequestAdapter.notifyItemRemoved(position);
                        teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        if (tempTeacherReceivedRequestsList.isEmpty()) {
                            teacherReceivedRequestsDialogLayoutBinding.noReceivedRequestsForTeacher.setVisibility(View.VISIBLE);
                            teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setVisibility(View.GONE);
                        } else {
                            teacherReceivedRequestsDialogLayoutBinding.noReceivedRequestsForTeacher.setVisibility(View.GONE);
                            teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setVisibility(View.VISIBLE);
                        }
                        teacherReceivedRequestDialog.dismiss();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else if (flag == 0) {
            database.insertTeacherCourse(new TeacherPostRequest(email, tempRequestId,
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getCourses(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getEducationLevel(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getDuration(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getAvailability(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getLocation(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getTeachingMethod(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getStartDate(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getEndDate(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getStartTime(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getEndTime(),
                    tempTeacherReceivedRequestObject.getTeacherPostRequest().getPrice()),
                    tempTeacherReceivedRequestObject.getParent().getEmail(),
                    tempTeacherReceivedRequestObject.getChildren().get(0).getChildId(),
                    this);

            int position = tempTeacherReceivedRequestsList.indexOf(tempTeacherReceivedRequestObject);
            tempTeacherReceivedRequestsList.remove(tempTeacherReceivedRequestObject);
            teacherReceivedRequestAdapter.notifyItemRemoved(position);
            teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            if (tempTeacherReceivedRequestsList.isEmpty()) {
                teacherReceivedRequestsDialogLayoutBinding.noReceivedRequestsForTeacher.setVisibility(View.VISIBLE);
                teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setVisibility(View.GONE);
            } else {
                teacherReceivedRequestsDialogLayoutBinding.noReceivedRequestsForTeacher.setVisibility(View.GONE);
                teacherReceivedRequestsDialogLayoutBinding.teacherReceivedRequestsRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            MyAlertDialog.errorDialog(getContext());
        }
    }

    @Override
    public void onCourseAdded(int flag) {
        if (flag == 1) {
            MyAlertDialog.showDialogForDone(getContext(),"Course Added","Courses Added to your current courses , check the main page ..");
            database.getAllTeacherCourses(email,this);
        } else {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error Adding Course","Something went wrong , the new course is not added , please try again later ..");
        }
    }


    @Override
    public void onRequestSent(int flag) {
        if (flag == 1) {
            new Handler().postDelayed(() -> {
                dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setVisibility(View.VISIBLE);
                dialogTeacherMatchingOnCardClickedBinding.requestSentView.setVisibility(View.GONE);
                parentPostedRequestsForTeacherDialog.show();
                binding.loadingProgressBar2.setVisibility(View.GONE);
            }, 400);

            dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setOnClickListener(x -> {
                database.getAllTeacherCoursesDatesBeforeSendRequest(email,this);
            });
        } else if (flag == 0) {
            new Handler().postDelayed(() -> {
                dialogTeacherMatchingOnCardClickedBinding.requestSentView.setVisibility(View.VISIBLE);
                dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setVisibility(View.GONE);
                parentPostedRequestsForTeacherDialog.show();
                binding.loadingProgressBar2.setVisibility(View.GONE);
            }, 400);
        } else if (flag == -1) {
            new Handler().postDelayed(() -> {
                dialogTeacherMatchingOnCardClickedBinding.requestSentView.setVisibility(View.VISIBLE);
                dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setVisibility(View.GONE);
                parentPostedRequestsForTeacherDialog.show();
                binding.loadingProgressBar2.setVisibility(View.GONE);
            }, 400);
        } else {
            new Handler().postDelayed(() -> {
                dialogTeacherMatchingOnCardClickedBinding.requestSentView.setVisibility(View.VISIBLE);
                dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setVisibility(View.GONE);
                parentPostedRequestsForTeacherDialog.show();
                binding.loadingProgressBar2.setVisibility(View.GONE);
            }, 400);
        }
    }



    @Override
    public void onTeacherToParentRequestSent(int flag) {
        if (flag == 0) {
            MyAlertDialog.warningDialog(getContext(), "Request Sent Before", "You Sent A request to this parent before wait for him to response Or Remove The Request");
        } else if (flag == 1) {
            //parentPostedRequestsForTeacherDialog.dismiss();
            MyAlertDialog.showDialogForDone(getContext(), "Request Sent", "Request Sent To Parent , Wait For The response ..");
            dialogTeacherMatchingOnCardClickedBinding.requestSentView.setVisibility(View.VISIBLE);
            dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setVisibility(View.GONE);
            dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setOnClickListener(x -> {
               // database.addTeacherSentRequestToParent(email, tempTeacherMatchModelForCheckTeacherSentRequest, this);
                database.getAllTeacherCoursesDatesBeforeSendRequest(email,this);
            });

        } else if (flag == -1) {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Error", "Something Went Wrong , Please try again later ..");
        } else {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Network Error", "Connection Error, Please try again later ..");
        }
    }

    @Override
    public void onSentRequestDeleted(int flag) {
        if (flag == 0) {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "No Request", "This Request May Be Deleted Before ..Try Again Later");
            parentPostedRequestsForTeacherDialog.dismiss();
        } else if (flag == 1) {
            dialogTeacherMatchingOnCardClickedBinding.requestSentView.setVisibility(View.GONE);
            dialogTeacherMatchingOnCardClickedBinding.sendRequestBtn.setVisibility(View.VISIBLE);
        } else if (flag == -1) {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Request Error", "An Error Occurred ,This Request May Be Deleted Before ..");
        } else if (flag == 2) {
            MyAlertDialog.warningDialog(getContext(), "Unable To Delete", "This Request Is Accepted By Parent , communicate with parent to delete it ..");
        } else {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Network Error", "A Network Error Occurred , Please try again Later ..");
            parentPostedRequestsForTeacherDialog.dismiss();
        }
    }

    private void showFilterDialogForTeacher() {
        if (getContext() != null) {
            filterLayoutBinding = FilterLayoutBinding.inflate(LayoutInflater.from(getContext()));
            filterDialog = new Dialog(getContext());
            filterDialog.setContentView(filterLayoutBinding.getRoot());
            filterDialog.setCancelable(false);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(filterDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 2500;
            filterDialog.getWindow().setAttributes(layoutParams);
            if (filterDialog.getWindow() != null)
                filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            isFilterDialogShowing = true;
            filterDialog.show();

            filterLayoutBinding.closeImage.setOnClickListener(z -> {
                isFilterDialogShowing=false;
                filterDialog.dismiss();
            });

            filterLayoutBinding.filterCancelBtn.setOnClickListener(c -> {
                isFilterDialogShowing=false;
                filterDialog.dismiss();
            });

            filterSelectedLocationList.clear();
            filterSelectedGradeList.clear();
            filterSelectedGenderList.clear();
            filterSelectedCoursesList.clear();
            filterTeachingMethodList.clear();


            setLocationFlexBox();
            setCoursesFlexBox();
            setGenderFlexBox();
            setChildGradeFlexBox();
            setTeachingMethodFlexBox();

            filterLayoutBinding.locationEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setFlexBoxEnabled(filterLayoutBinding.locationFlexBox1, s.toString().isEmpty());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            filterLayoutBinding.coursesEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setFlexBoxEnabled(filterLayoutBinding.coursesFlexBox, s.toString().isEmpty());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            filterLayoutBinding.filterConfirmBtn.setOnClickListener(Z -> {
                isFilterDialogShowing=false;
                FilterCriteria criteria = new FilterCriteria();
                criteria.setLocation(filterLayoutBinding.locationEditText.getText().toString().trim());
                criteria.setCourse(filterLayoutBinding.coursesEditText.getText().toString().trim());
                criteria.setLocationList(filterSelectedLocationList);
                criteria.setCoursesList(filterSelectedCoursesList);
                criteria.setGradeList(filterSelectedGradeList);
                criteria.setGenderList(filterSelectedGenderList);
                criteria.setTeachingMethodList(filterTeachingMethodList);
                criteria.setMinPrice(filterLayoutBinding.priceFromEditText.getText().toString().isEmpty() ? null : Double.parseDouble(filterLayoutBinding.priceFromEditText.getText().toString()));
                criteria.setMinPrice(filterLayoutBinding.priceToEditText.getText().toString().isEmpty() ? null : Double.parseDouble(filterLayoutBinding.priceToEditText.getText().toString()));
                List<TeacherMatchModel> filteredList = filter(parentPostedRequestsForTeacherList,criteria);
                assert getView() != null ;
                if (filteredList.isEmpty()) {
                    binding.noPostedRequestTextView.setText(getString(R.string.noMatchedDataString));
                    binding.noPostedRequestTextView.setVisibility(View.VISIBLE);
                    binding.addedCoursesRecyclerView.setVisibility(View.GONE);
                    Snackbar.make(getView(), "No Filter Matching Data", Snackbar.LENGTH_SHORT).setDuration(500).show();

                } else {
                    binding.noPostedRequestTextView.setText(getString(R.string.noPostedRequestsString));
                    binding.noPostedRequestTextView.setVisibility(View.GONE);
                    binding.addedCoursesRecyclerView.setVisibility(View.VISIBLE);
                    matchingTeacherAdapter.filteredList(filteredList);
                    Snackbar.make(getView(), "Data Filtered", Snackbar.LENGTH_SHORT).setDuration(500).show();
                }
                filterDialog.dismiss();
            });
        }
    }

    private List<TeacherMatchModel> filter(List<TeacherMatchModel> listToFilter, FilterCriteria criteria){
        List<TeacherMatchModel> filteredList = new ArrayList<>();

        for (TeacherMatchModel teacher : listToFilter) {
            boolean matches = true;

            if (criteria.getLocation() != null && !criteria.getLocation().isEmpty()) {
                matches = matches && teacher.getLocation().trim().toLowerCase().contains(criteria.getLocation().trim().toLowerCase());
            }

            if (criteria.getCourse() != null && !criteria.getCourse().isEmpty()) {
                matches = matches && teacher.getCourses().trim().toLowerCase().contains(criteria.getCourse().trim().toLowerCase());
            }

            if (criteria.getLocationList() != null && !criteria.getLocationList().isEmpty()) {
                if (!criteria.getLocationList().contains("any")) {
                    matches = matches && criteria.getLocationList().contains(teacher.getLocation());
                }
            }

            if (criteria.getCoursesList() != null && !criteria.getCoursesList().isEmpty()) {
                if (!criteria.getCoursesList().contains("any")) {
                    matches = matches && criteria.getCoursesList().contains(teacher.getCourses());
                }
            }

            if (criteria.getGenderList() != null && !criteria.getGenderList().isEmpty()) {
                if (!criteria.getGenderList().contains("any")) {
                    String teacherGender = teacher.getCustomChildData().getGender() == 0 ? "Female" : "Male";
                    matches = matches && criteria.getGenderList().contains(teacherGender);
                }
            }

            if (criteria.getGradeList() != null && !criteria.getGradeList().isEmpty()) {
                if (!criteria.getGradeList().contains("any")) {
                    matches = matches && criteria.getGradeList().contains(String.valueOf(teacher.getCustomChildData().getChildGrade()));
                }
            }

            if (criteria.getTeachingMethodList() != null && !criteria.getTeachingMethodList().isEmpty()) {
                if (!criteria.getTeachingMethodList().contains("any")) {
                    matches = matches && criteria.getTeachingMethodList().contains(teacher.getTeachingMethod());
                }
            }

            if (criteria.getMinPrice() != null) {
                matches = matches && teacher.getPriceMinimum() >= criteria.getMinPrice();
            }

            if (criteria.getMaxPrice() != null) {
                matches = matches && teacher.getPriceMaximum() <= criteria.getMaxPrice();
            }

            if (matches) {
                filteredList.add(teacher);
            }
        }

        return filteredList;
    }



    private void setFlexBoxEnabled(FlexboxLayout flexboxLayout, Boolean status) {
        for (int i = 0; i < flexboxLayout.getChildCount(); i++) {
            flexboxLayout.getChildAt(i).setEnabled(status);
        }
    }


    private boolean checkIfButtonInList(List<String> list, String selectedCourse) {
        for (String str : list) {
            if (str.equalsIgnoreCase(selectedCourse)) {
                return true;
            }
        }
        return false;
    }

    private void setLocationFlexBox() {
        assert getContext() != null;
        String[] locationArrayForFilter = getResources().getStringArray(R.array.locationArray);
        if (filterLayoutBinding.locationFlexBox1.getChildCount() > 0)
            filterLayoutBinding.locationFlexBox1.removeAllViews();

        if (!filterSelectedLocationList.isEmpty())
            filterSelectedLocationList.clear();


        for (String str : locationArrayForFilter) {
            AppCompatButton appCompatButton = new AppCompatButton(getContext());
            appCompatButton.setAllCaps(false);
            appCompatButton.setText(str);
            appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                            getResources().getDisplayMetrics()),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            appCompatButton.setGravity(Gravity.CENTER);
            appCompatButton.setLayoutParams(layoutParams);

            int paddingDp = 8; // Example padding in dp
            int paddingPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
            appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


            FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int marginDp = 8;
            int marginPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
            flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
            appCompatButton.setLayoutParams(flexboxLayoutParams);

            appCompatButton.setOnClickListener(s -> {
                if (checkIfButtonInList(filterSelectedLocationList, appCompatButton.getText().toString())) {
                    appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                    filterSelectedLocationList.remove(appCompatButton.getText().toString());
                } else {
                    appCompatButton.setBackgroundResource(R.drawable.selected_view);
                    filterSelectedLocationList.add(appCompatButton.getText().toString());
                }
            });
            filterLayoutBinding.locationFlexBox1.addView(appCompatButton);
        }
    }


    private void setCoursesFlexBox() {
        String[] coursesArrayForFilter = getResources().getStringArray(R.array.allCourses);
        if (filterLayoutBinding.coursesFlexBox.getChildCount() > 0)
            filterLayoutBinding.coursesFlexBox.removeAllViews();

        if (!filterSelectedCoursesList.isEmpty())
            filterSelectedCoursesList.clear();

        if (getContext() != null) {
            for (String str : coursesArrayForFilter) {
                AppCompatButton appCompatButton = new AppCompatButton(getContext());
                appCompatButton.setAllCaps(false);
                appCompatButton.setText(str);
                appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                                getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                appCompatButton.setGravity(Gravity.CENTER);
                appCompatButton.setLayoutParams(layoutParams);

                int paddingDp = 8;
                int paddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
                appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


                FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginDp = 8;
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                appCompatButton.setLayoutParams(flexboxLayoutParams);

                appCompatButton.setOnClickListener(s -> {
                    if (checkIfButtonInList(filterSelectedCoursesList, appCompatButton.getText().toString())) {
                        appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                        filterSelectedCoursesList.remove(appCompatButton.getText().toString());
                    } else {
                        appCompatButton.setBackgroundResource(R.drawable.selected_view);
                        filterSelectedCoursesList.add(appCompatButton.getText().toString());
                    }
                });
                filterLayoutBinding.coursesFlexBox.addView(appCompatButton);
            }
        }
    }

    private void setGenderFlexBox() {
        if (getContext() != null) {
            String[] childGenderArrayForFilter = getResources().getStringArray(R.array.childGenderFilter);
            if (filterLayoutBinding.childrenGenderFlexBox.getChildCount() > 0) {
                filterLayoutBinding.childrenGenderFlexBox.removeAllViews();
            }

            if (!filterSelectedGenderList.isEmpty())
                filterSelectedGenderList.clear();

            for (String str : childGenderArrayForFilter) {
                AppCompatButton appCompatButton = new AppCompatButton(getContext());
                appCompatButton.setAllCaps(false);
                appCompatButton.setText(str.trim());
                appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                                getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                appCompatButton.setGravity(Gravity.CENTER);
                appCompatButton.setLayoutParams(layoutParams);

                int paddingDp = 8;
                int paddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
                appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


                FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginDp = 8;
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                appCompatButton.setLayoutParams(flexboxLayoutParams);

                appCompatButton.setOnClickListener(C -> {
                    if (checkIfButtonInList(filterSelectedGenderList, appCompatButton.getText().toString())) {
                        appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                        filterSelectedGenderList.remove(appCompatButton.getText().toString());
                    } else {
                        appCompatButton.setBackgroundResource(R.drawable.selected_view);
                        filterSelectedGenderList.add(appCompatButton.getText().toString());
                    }
                });
                filterLayoutBinding.childrenGenderFlexBox.addView(appCompatButton);
            }
        }
    }

    private void setChildGradeFlexBox() {
        if (getContext() != null) {
            String[] childGradeArrayForFilter = getResources().getStringArray(R.array.childGradeFilter);
            if (filterLayoutBinding.childrenGradeFlexBox.getChildCount() > 0) {
                filterLayoutBinding.childrenGradeFlexBox.removeAllViews();
            }
            if (!filterSelectedGradeList.isEmpty())
                filterSelectedGradeList.clear();

            for (String str : childGradeArrayForFilter) {
                AppCompatButton appCompatButton = new AppCompatButton(getContext());
                appCompatButton.setAllCaps(false);
                appCompatButton.setText(str.trim());
                appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                                getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                appCompatButton.setGravity(Gravity.CENTER);
                appCompatButton.setLayoutParams(layoutParams);

                int paddingDp = 8;
                int paddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
                appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


                FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginDp = 8;
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                appCompatButton.setLayoutParams(flexboxLayoutParams);

                appCompatButton.setOnClickListener(C -> {
                    if (str.equalsIgnoreCase("Any")) {
                        if (checkIfButtonInList(filterSelectedGradeList, "Any")) {

                            appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                            filterSelectedGradeList.remove("Any");
                        } else {
                            appCompatButton.setBackgroundResource(R.drawable.selected_view);
                            filterSelectedGradeList.add("Any");
                        }
                    } else {
                        int number = 0;
                        Pattern pattern = Pattern.compile("\\d+");
                        Matcher matcher = pattern.matcher(appCompatButton.getText().toString());
                        if (matcher.find())
                            number = Integer.parseInt(matcher.group());

                        if (checkIfButtonInList(filterSelectedGradeList, number + "")) {

                            appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                            filterSelectedGradeList.remove(number + "");
                        } else {
                            appCompatButton.setBackgroundResource(R.drawable.selected_view);
                            filterSelectedGradeList.add(number + "");
                        }
                    }
                });
                filterLayoutBinding.childrenGradeFlexBox.addView(appCompatButton);
            }
        }
    }

    private void setTeachingMethodFlexBox() {
        String[] teachingMethodArrayForFilter = getResources().getStringArray(R.array.teachingMethodsFilter);
        if (getContext() != null) {
            if (filterLayoutBinding.teachingMethodFlexBox.getChildCount() > 0)
                filterLayoutBinding.teachingMethodFlexBox.removeAllViews();

            if (!filterTeachingMethodList.isEmpty())
                filterTeachingMethodList.clear();


            for (String str : teachingMethodArrayForFilter) {
                AppCompatButton appCompatButton = new AppCompatButton(getContext());
                appCompatButton.setAllCaps(false);
                appCompatButton.setText(str.trim());
                appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                                getResources().getDisplayMetrics()),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                appCompatButton.setGravity(Gravity.CENTER);
                appCompatButton.setLayoutParams(layoutParams);
                int paddingDp = 8;
                int paddingPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
                appCompatButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);


                FlexboxLayout.LayoutParams flexboxLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                int marginDp = 8;
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                flexboxLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);
                appCompatButton.setLayoutParams(flexboxLayoutParams);
                appCompatButton.setOnClickListener(C -> {
                    if (checkIfButtonInList(filterTeachingMethodList, appCompatButton.getText().toString())) {
                        appCompatButton.setBackgroundResource(R.drawable.rounded_corners);
                        filterTeachingMethodList.remove(appCompatButton.getText().toString());
                    } else {
                        appCompatButton.setBackgroundResource(R.drawable.selected_view);
                        filterTeachingMethodList.add(appCompatButton.getText().toString());
                    }
                });
                filterLayoutBinding.teachingMethodFlexBox.addView(appCompatButton);
            }
        }
    }

    @Override
    public void onTeacherCoursesFetched(int flag, JSONArray coursesDatesTeacherTable) {
        if(flag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","An Error Occurred Please Try Again Later ..");
            binding.loadingProgressBar2.setVisibility(View.GONE);
        }
        else if(flag == -2){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Connection Error","Connection is Unstable , please try again later ..");
            binding.loadingProgressBar2.setVisibility(View.GONE);
        }
        else if(flag == 1){
            if(/*coursesDateParentTable != null && */coursesDatesTeacherTable != null){
                try {
                    if(coursesDatesTeacherTable.length() > 0/* && coursesDateParentTable.length() > 0*/){
                        int conflictFlag = 0;
                        for(int i=0;i<coursesDatesTeacherTable.length() ; i++){
                            JSONObject jsonObject = coursesDatesTeacherTable.getJSONObject(i);
                            String startDate = jsonObject.getString("startDate");
                            String endDate = jsonObject.getString("endDate");
                            String startTime = jsonObject.getString("startTime");
                            String endTime = jsonObject.getString("endTime");
                            String availability = jsonObject.getString("availabilityForJob");
                            String days = availability;
                            if (availability.equalsIgnoreCase("Weekend")) {
                                days = "Thur , Fri";
                            } else if (availability.equalsIgnoreCase("Any")) {
                                days = "Sat , Sun , Mon , Tues , Thur , Fri";
                            }
                            if (availability.charAt(availability.length() - 1) == ',') {
                                days = availability.substring(0, availability.length() - 1).trim();
                            }

                            if (DateUtils.isConflict(tempRequestToSendDateTimeModel, new DateTimeModel(startDate, endDate, startTime, endTime, days))) {
                               // MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Course Conflict", "This Request Make A conflict with one of your existing courses");
                                conflictFlag = 1;
                                break;
                            }
                        }
                        /*if(conflictFlag == 0){
                            Log.d("33333333333333333333333333","33333333333333333333333333");
                            for(int i = 0 ; i < coursesDateParentTable.length() ; i++){
                                JSONObject jsonObject = coursesDateParentTable.getJSONObject(i);
                                String startDate = jsonObject.getString("startDate");
                                String endDate = jsonObject.getString("endDate");
                                String startTime = jsonObject.getString("startTime");
                                String endTime = jsonObject.getString("endTime");
                                String availability = jsonObject.getString("choseDays");
                                String days = availability;
                                if (availability.equalsIgnoreCase("Weekend")) {
                                    days = "Thur , Fri";
                                } else if (availability.equalsIgnoreCase("Any")) {
                                    days = "Sat , Sun , Mon , Tues , Thur , Fri";
                                }
                                if (availability.charAt(availability.length() - 1) == ',') {
                                    days = availability.substring(0, availability.length() - 1).trim();
                                }

                                if (DateUtils.isConflict(tempRequestToSendDateTimeModel, new DateTimeModel(startDate, endDate, startTime, endTime, days))) {
                                   // MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Course Conflict", "This Request Make A conflict with one of your existing courses");
                                    Log.d("44444444444444444444444444444","44444444444444444444444444444");
                                    conflictFlag = 1;
                                    break;
                                }
                            }
                        }*/
                        if(conflictFlag == 1){
                            Log.d("555555555555555555555555","555555555555555555555555");
                            MyAlertDialog.warningDialog(getContext(),"Conflict Courses","This Course Make A Confliction With One Of Your existing Courses ..");
                            binding.loadingProgressBar2.setVisibility(View.GONE);
                        }
                        else {
                            Log.d("6666666666666666666666666","6666666666666666666666666");
                            //  database.checkIfTeacherRequestSentBefore(email, currentCourseToSendRequestMatchModel, this);
                            database.addTeacherSentRequestToParent(email, tempTeacherMatchModelForCheckTeacherSentRequest, this);
                        }

                    }
                    /*else if(coursesDatesTeacherTable.length() > 0/* && coursesDateParentTable.length() == 0){
                        int conflictFlag = 0;
                        for(int i=0;i<coursesDatesTeacherTable.length() ; i++){
                            JSONObject jsonObject = coursesDatesTeacherTable.getJSONObject(i);
                            String startDate = jsonObject.getString("startDate");
                            String endDate = jsonObject.getString("endDate");
                            String startTime = jsonObject.getString("startTime");
                            String endTime = jsonObject.getString("endTime");
                            String availability = jsonObject.getString("availabilityForJob");
                            String days = availability;
                            if (availability.equalsIgnoreCase("Weekend")) {
                                days = "Thur , Fri";
                            } else if (availability.equalsIgnoreCase("Any")) {
                                days = "Sat , Sun , Mon , Tues , Thur , Fri";
                            }
                            if (availability.charAt(availability.length() - 1) == ',') {
                                days = availability.substring(0, availability.length() - 1).trim();
                            }

                            if (DateUtils.isConflict(tempRequestToSendDateTimeModel, new DateTimeModel(startDate, endDate, startTime, endTime, days))) {
                               // MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Course Conflict", "This Request Make A conflict with one of your existing courses");
                                conflictFlag = 1;
                                break;
                            }
                        }
                        if(conflictFlag == 1){
                            MyAlertDialog.warningDialog(getContext(),"Conflict Courses","This Course Make A Confliction With One Of Your existing Courses ..");
                            binding.loadingProgressBar2.setVisibility(View.GONE);
                        }
                        else {
                          //  database.checkIfTeacherRequestSentBefore(email, currentCourseToSendRequestMatchModel, this);
                            database.addTeacherSentRequestToParent(email, tempTeacherMatchModelForCheckTeacherSentRequest, this);
                        }
                    }*/
                   /* else if(coursesDateParentTable.length() > 0 && coursesDatesTeacherTable.length() == 0){
                        int conflictFlag = 0;
                        for(int i = 0 ; i < coursesDateParentTable.length() ; i++){
                            JSONObject jsonObject = coursesDateParentTable.getJSONObject(i);
                            String startDate = jsonObject.getString("startDate");
                            String endDate = jsonObject.getString("endDate");
                            String startTime = jsonObject.getString("startTime");
                            String endTime = jsonObject.getString("endTime");
                            String availability = jsonObject.getString("choseDays");
                            String days = availability;
                            if (availability.equalsIgnoreCase("Weekend")) {
                                days = "Thur , Fri";
                            } else if (availability.equalsIgnoreCase("Any")) {
                                days = "Sat , Sun , Mon , Tues , Thur , Fri";
                            }
                            if (availability.charAt(availability.length() - 1) == ',') {
                                days = availability.substring(0, availability.length() - 1).trim();
                            }
                            if (DateUtils.isConflict(tempRequestToSendDateTimeModel, new DateTimeModel(startDate, endDate, startTime, endTime, days))) {
                                // MyAlertDialog.showCustomAlertDialogLoginError(getContext(), "Course Conflict", "This Request Make A conflict with one of your existing courses");
                                conflictFlag = 1;
                                break;
                            }
                        }
                        if(conflictFlag == 1){
                            MyAlertDialog.warningDialog(getContext(),"Conflict Courses","This Course Make A Confliction With One Of Your existing Courses ..");
                            binding.loadingProgressBar2.setVisibility(View.GONE);
                        }
                        else {
                        //    database.checkIfTeacherRequestSentBefore(email, currentCourseToSendRequestMatchModel, this);
                            database.addTeacherSentRequestToParent(email, tempTeacherMatchModelForCheckTeacherSentRequest, this);
                        }
                    }*/
                    else {
                      //  database.checkIfTeacherRequestSentBefore(email, currentCourseToSendRequestMatchModel, this);
                        database.addTeacherSentRequestToParent(email, tempTeacherMatchModelForCheckTeacherSentRequest, this);
                    }
                }
                catch (Exception e){
                    MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","An Error Occurred Please Try Again Later ..");
                }
            }
            else {
                MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","Error fetching data , please try again after 5 minutes ...");
            }
        }
    }

    @Override
    public void onCoursesFetched(int flag, JSONArray courses) {
        if(flag == 0){
            binding.noDataAddedText.setVisibility(View.VISIBLE);
            binding.addedCoursesRecyclerView.setVisibility(View.GONE);
        }
        else if(flag == 1){
            if(!teacherCoursesList.isEmpty()){
                teacherCoursesList.clear();
            }
            if(!expiredCoursesList.isEmpty()){
                expiredCoursesList.clear();
            }
            try {
                List<Address> addressList1 = new ArrayList<>();
                List<String> phoneNumberList1 = new ArrayList<>();
                if(courses.length() > 0){
                    for(int i = courses.length() - 1 ; i >= 0 ; i--){
                        JSONObject jsonObject = courses.getJSONObject(i);
                        int courseId = jsonObject.getInt("courseId");
                        String teacherEmail = jsonObject.getString("teacherEmail");
                        String parentEmail = jsonObject.getString("parentEmail");
                        int teacherSentRequestId = jsonObject.getInt("teacherSentRequestId");
                        int parentSentRequestId = jsonObject.getInt("parentSentRequestId");
                        String coursesStr = jsonObject.getString("courses");
                        String educationLevel = jsonObject.getString("educationLevel");
                        int duration = jsonObject.getInt("duration");
                        String availabilityForJob = jsonObject.getString("availabilityForJob");
                        String location = jsonObject.getString("location");
                        String teachingMethod = jsonObject.getString("teachingMethod");
                        String startTime = jsonObject.getString("startTime");
                        String endTime = jsonObject.getString("endTime");
                        String startDate = jsonObject.getString("startDate");
                        String endDate = jsonObject.getString("endDate");
                        double price = jsonObject.getDouble("price");
                        int childId = jsonObject.getInt("childId");
                        String childName = jsonObject.getString("childName");
                        String childAge = jsonObject.getString("childAge");
                        String childGender = jsonObject.getString("childGender");
                        String childGrade = jsonObject.getString("childGrade");
                        String firstNameStr = jsonObject.getString("firstName");
                        String tempParentFirstName = firstNameStr.substring(0,1).toUpperCase()+firstNameStr.substring(1).toLowerCase();
                        String parentLastNameStr = jsonObject.getString("lastName");
                        String tempParentLastNameStr = parentLastNameStr.substring(0,1).toUpperCase()+parentLastNameStr.substring(1).toLowerCase();
                        if(i == courses.length() -1){
                            String addressTemp = jsonObject.getString("addresses");
                            if(addressTemp.contains("|")){
                                String[] splitAddress = addressTemp.split("\\|");
                                for(String str : splitAddress){
                                    String[] sp = str.split(",");
                                    addressList1.add(new Address(sp[0],sp[1]));
                                }
                            }
                            else {
                                String[] sp = addressTemp.split(",");
                                addressList1.add(new Address(sp[0],sp[1]));
                            }

                            String phoneTemp = jsonObject.getString("phoneNumbers");
                            if(phoneTemp.contains(",")){
                                String[] sp = phoneTemp.split(",");
                                phoneNumberList1.addAll(Arrays.asList(sp));
                            }
                            else {
                                phoneNumberList1.add(phoneTemp);
                            }
                        }

                        if(!DateUtils.isDateTimeExpired(startDate,endDate,startTime,endTime)){
                            teacherCoursesList.add(new Course(courseId,teacherEmail,parentEmail,
                                    parentSentRequestId,teacherSentRequestId,
                                    childId,coursesStr,duration,availabilityForJob,
                                    location,educationLevel,teachingMethod,startTime,endTime,
                                    startDate,endDate,price,
                                    new Children(childId,childName,childAge,Integer.parseInt(childGender),Integer.parseInt(childGrade)),
                                    new Parent(parentEmail,tempParentFirstName,tempParentLastNameStr,addressList1,phoneNumberList1),
                                    null));
                        }
                        else {
                            expiredCoursesList.add(new ExpiredCourse(courseId,teacherSentRequestId,parentSentRequestId));
                        }

                    }
                    setMyCoursesAdapter();
                    binding.refreshRecyclerView.setRefreshing(false);
                }
                else {
                    MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Connection Error","Connection Error , Unable to fetch the data at the moment , please try again later");
                }
            }
            catch (Exception e){
               // MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","An Error Occurred, Unable to fetch the data at the moment , please try again later");
                throw new RuntimeException(e);
            }

        }
        else if(flag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","An Error Occurred, Unable to fetch the data at the moment , please try again later");
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Connection Error","Connection Error , Unable to fetch the data at the moment , please try again later");
        }
    }

    @Override
    public void onTeacherCourseClicked(Course course) {
        if(getContext() != null){
            courseDialog = new Dialog(getContext());
            TeacherCourseCardClickedLayoutBinding teacherCourseCardClickedLayoutBinding = TeacherCourseCardClickedLayoutBinding.inflate(LayoutInflater.from(getContext()));
            courseDialog.setContentView(teacherCourseCardClickedLayoutBinding.getRoot());
            courseDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(courseDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 2300;
            courseDialog.getWindow().setAttributes(layoutParams);

            if(courseDialog.getWindow() != null)
                courseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            courseDialog.show();

            teacherCourseCardClickedLayoutBinding.closeImageView.setOnClickListener(c0->{
                courseDialog.dismiss();
            });

            teacherCourseCardClickedLayoutBinding.coursesTextView.setText(course.getCourses());
            teacherCourseCardClickedLayoutBinding.dateTextView.setText(course.getStartDate()+"  -  "+course.getEndDate());
            teacherCourseCardClickedLayoutBinding.timeTextView.setText(course.getStartTime()+"  -  "+course.getEndTime());
            teacherCourseCardClickedLayoutBinding.choseDaysTextView.setText(course.getDays());
            teacherCourseCardClickedLayoutBinding.teachingMethodTextView.setText(course.getTeachingMethod());
            teacherCourseCardClickedLayoutBinding.childNameTextView.setText(course.getChild().getChildName());
            teacherCourseCardClickedLayoutBinding.coursesTextView.setText(course.getCourses());
            if(course.getChild().getChildAge().equalsIgnoreCase("1")){
                teacherCourseCardClickedLayoutBinding.childAgeTextView.setText(String.format("%s Year", course.getChild().getChildAge()));
            }
            else {
                teacherCourseCardClickedLayoutBinding.childAgeTextView.setText(String.format("%s Years", course.getChild().getChildAge()));
            }
            String gender = "Male";
            if(course.getChild().getChildGender() == 0){
                gender = "Female";
            }
            teacherCourseCardClickedLayoutBinding.childGenderTextView.setText(gender);
            if(course.getChild().getGrade() == 1){
                teacherCourseCardClickedLayoutBinding.childGradeTextView.setText(String.format("%dst Grade", course.getChild().getGrade()));
            }
            else if(course.getChild().getGrade() == 2){
                teacherCourseCardClickedLayoutBinding.childGradeTextView.setText(String.format("%dnd Grade", course.getChild().getGrade()));
            }
            else {
                teacherCourseCardClickedLayoutBinding.childGradeTextView.setText(String.format("%dth Grade", course.getChild().getGrade()));
            }

            teacherCourseCardClickedLayoutBinding.parentNameTextView.setText(course.getParent().getFirstName()+" "+course.getParent().getLastName());
            teacherCourseCardClickedLayoutBinding.parentEmailTextView.setText(course.getParentEmail());
            StringBuilder str = new StringBuilder();
            List<String> phone = course.getParent().getPhoneNumbersList();
            for(int i = 0 ; i< phone.size() ; i++){
                if(i + 1 == phone.size()){
                    str.append(phone.get(i));
                }
                else {
                    str.append(phone.get(i)).append(" — ");
                }
            }
            teacherCourseCardClickedLayoutBinding.parentPhoneTextView.setText(str);

            StringBuilder str2 = new StringBuilder();
            List<Address> address = course.getParent().getAddressList();
            for(int i = 0 ; i < address.size();i++){
                if(i+1 == address.size()){
                    str2.append(" — ").append(address.get(i).getCity()).append(" , ").append(address.get(i).getCountry());
                }
                else {
                    str2.append(" — ").append(i + 1).append(address.get(i).getCity()).append(" , ").append(address.get(i).getCountry()).append("\n");
                }
            }
            teacherCourseCardClickedLayoutBinding.parentAddressTextView.setText(str2);
            teacherCourseCardClickedLayoutBinding.cardSettings.setOnClickListener(c->{
                showCardSettings(teacherCourseCardClickedLayoutBinding,course);
            });
        }
    }

    private void showCardSettings(TeacherCourseCardClickedLayoutBinding teacherCourseCardClickedLayoutBinding,Course course){
        PopupMenu popupMenu = new PopupMenu(getContext(),teacherCourseCardClickedLayoutBinding.cardSettings);
        popupMenu.getMenuInflater().inflate(R.menu.menu_teacher_course,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item ->{
            if(item.getItemId() == R.id.markAsCompleted){
                completeTeacherCourse(course);
            }
            else if(item.getItemId() == R.id.deleteCourse){
              // sendRemoveRequestToParent(course);
                customRemoveConfirmAlterDialog(course);
                // To Do ()
            }

            return true;
        });
    }

    private void sendRemoveRequestToParent(Course course){
       // database.sendRemoveRequestToParent(course,this);
    }

    private void completeTeacherCourse(Course course){
        if(DateUtils.isDateTimeExpired(course.getStartDate(),course.getStartTime(),course.getEndDate(),course.getEndTime())){
            customConfirmAlterDialog(course);
            // delete from database ..
           // database.setCourseIsDone(course,this);
        }
        else {
            MyAlertDialog.warning(getContext(),"Not completed","Course Is Not completed yet , if you want to remove it choose Remove from Enrolled");
        }
    }

    private void customRemoveConfirmAlterDialog(Course course){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.warning_icon);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText("Confirm ");
        errorTextView.setText("Are you sure you want to send a delete request to the parent ?");
        errorTextView.setTextColor(getResources().getColor(R.color.black));
        builder.setView(view);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.sendRemoveRequestToParent(course,TeacherFragment.this);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void customConfirmAlterDialog(Course course){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.warning_icon);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText("Confirm ");
        errorTextView.setText("Are You Sure of setting this course to done ?");
        errorTextView.setTextColor(getResources().getColor(R.color.black));
        builder.setView(view);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.setCourseIsDone(course,TeacherFragment.this);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCourseDoneSet(int flag) {
        if(flag == 0){
            MyAlertDialog.showDialogForDone(getContext(),"No Course","Course Is No Longer exists or an error occurred..");
            courseDialog.dismiss();
            database.getAllTeacherCourses(email,this);
        }
        else if(flag == 1){
            MyAlertDialog.showDialogForDone(getContext(),"Done Course","Course Is set to done ..");
            courseDialog.dismiss();
            database.getAllTeacherCourses(email,this);
        }
        else if(flag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","An Error Occurred , please try again later ..");
        }
        else{
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Connection Error","Connection Error , please check your internet and try again ..");
        }
    }

    @Override
    public void onRemoveRequestSent(int flag) {
        if(flag == 0){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Course Error","Unable to find the course , please update the list and try again ..");
            courseDialog.dismiss();
            database.getAllTeacherCourses(email,this);
        }
        else if(flag == 1){
            MyAlertDialog.showDialogForDone(getContext(),"Request Sent","Request Sent To The Parent, when he/she accepts the request , the course will be deleted ..");
            courseDialog.dismiss();
        }
        else if(flag == 2){
            MyAlertDialog.warningDialog(getContext(),"Sent Before","Delete Request Sent before , wait for parent response ..");
        }
        else if(flag == -1){
            MyAlertDialog.showCustomAlertDialogSpinnerError(getContext(),"Error","An Error occurred sending request to the parent , please try again later");
        }
        else{
            MyAlertDialog.showCustomAlertDialogSpinnerError(getContext(),"Connection Error","Connection Error unable to access database, please try again later or check your network");
        }
    }

    @Override
    public void onCourseFetched(int flag, JSONArray courseInformation) {
        if(flag == 0){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"No Date","Unable to show the data , request may be deleted ..");
        }
        else if(flag == 1 && courseInformation != null &&courseInformation.length() == 1) {
            Course tempCourse = new Course();
            List<String> parentPhoneNumbersList = new ArrayList<>();
            try{
                JSONObject jsonObject = courseInformation.getJSONObject(0);
                int courseId = jsonObject.getInt("courseId");
                String teacherEmail = jsonObject.getString("teacherEmail");
                String parentEmail = jsonObject.getString("parentEmail");
                int teacherSentRequestId = jsonObject.getInt("teacherSentRequestId");
                int parentSentRequestId = jsonObject.getInt("parentSentRequestId");
                String courses = jsonObject.getString("courses");
                String availabilityForJob = jsonObject.getString("choseDays");
                String location = jsonObject.getString("location");
                String teachingMethod = jsonObject.getString("teachingMethod");
                String startTime = jsonObject.getString("startTime");
                String endTime = jsonObject.getString("endTime");
                String startDate = jsonObject.getString("startDate");
                String endDate = jsonObject.getString("endDate");
                double price = jsonObject.getDouble("price");
                int childId = jsonObject.getInt("childId");
                String childName = jsonObject.getString("childName");
                String childAge = jsonObject.getString("childAge");
                String childGender = jsonObject.getString("childGender");
                String childGrade = jsonObject.getString("childGrade");
                String parentFirstName = jsonObject.getString("firstName");
                parentFirstName = parentFirstName.substring(0,1).toUpperCase()+parentFirstName.substring(1).toLowerCase();
                String parentLastName = jsonObject.getString("lastName");
                parentLastName = parentLastName.substring(0,1).toUpperCase()+parentLastName.substring(1).toLowerCase();

                String phoneNumbers = jsonObject.getString("phoneNumbers");
                if (phoneNumbers.contains(",")) {
                    String[] splitPhoneNumbers = phoneNumbers.split(",");
                    parentPhoneNumbersList.addAll(Arrays.asList(splitPhoneNumbers));
                }
                else
                    parentPhoneNumbersList.add(phoneNumbers.trim());


                tempCourse.setCourseId(courseId);
                tempCourse.setTeacherEmail(teacherEmail);
                tempCourse.setParentEmail(parentEmail);
                tempCourse.setTeacherSentRequestId(teacherSentRequestId);
                tempCourse.setParentSentRequestId(parentSentRequestId);
                tempCourse.setCourses(courses);
                tempCourse.setDays(availabilityForJob);
                tempCourse.setLocation(location);
                tempCourse.setTeachingMethod(teachingMethod);
                tempCourse.setStartTime(startTime);
                tempCourse.setEndTime(endTime);
                tempCourse.setStartDate(startDate);
                tempCourse.setEndDate(endDate);
                tempCourse.setPrice(price);
                tempCourse.setChildId(childId);
                tempCourse.setChild(new Children(childId,childName,childAge,Integer.parseInt(childGender),Integer.parseInt(childGrade)));
                tempCourse.setParent(new Parent(parentEmail,parentFirstName,parentLastName,parentPhoneNumbersList,0));
                showTeacherRemoveReceivedCourseRequest(tempCourse);
            }
            catch(JSONException e){
                throw new RuntimeException(e);
            }
        }
        else if(flag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","Error fetching data , try again later.");
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Connection Error","Error connecting to database , please check your network ..");
        }
    }

    private void showTeacherRemoveReceivedCourseRequest(Course tempCourse){

        if(getContext() != null){
            Dialog removeRequestForTeacherDialog = new Dialog(getContext());
            DeleteTeacherCourseCardForTeacherBinding deleteTeacherCourseCardForTeacherBinding = DeleteTeacherCourseCardForTeacherBinding.inflate(LayoutInflater.from(getContext()));
            removeRequestForTeacherDialog.setContentView(deleteTeacherCourseCardForTeacherBinding.getRoot());
            removeRequestForTeacherDialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(removeRequestForTeacherDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 2300;
            removeRequestForTeacherDialog.getWindow().setAttributes(layoutParams);
            if(removeRequestForTeacherDialog.getWindow() != null)
                removeRequestForTeacherDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            removeRequestForTeacherDialog.show();

            deleteTeacherCourseCardForTeacherBinding.closeImageView.setOnClickListener(c->{
                removeRequestForTeacherDialog.dismiss();
            });

            deleteTeacherCourseCardForTeacherBinding.coursesTextView.setText(tempCourse.getCourses());
            deleteTeacherCourseCardForTeacherBinding.dateTextView.setText(tempCourse.getStartDate()+"  -  "+tempCourse.getEndDate());
            deleteTeacherCourseCardForTeacherBinding.timeTextView.setText(tempCourse.getStartTime()+"  -  "+tempCourse.getEndTime());
            deleteTeacherCourseCardForTeacherBinding.choseDaysTextView.setText(tempCourse.getDays());
            deleteTeacherCourseCardForTeacherBinding.teachingMethodTextView.setText(tempCourse.getTeachingMethod());
            deleteTeacherCourseCardForTeacherBinding.childNameTextView.setText(tempCourse.getChild().getChildName());

            if(tempCourse.getChild().getChildAge().equalsIgnoreCase("1")){
                deleteTeacherCourseCardForTeacherBinding.childAgeTextView.setText(String.format("%s Year", tempCourse.getChild().getChildAge()));
            }
            else {
                deleteTeacherCourseCardForTeacherBinding.childAgeTextView.setText(String.format("%s Years", tempCourse.getChild().getChildAge()));
            }
            String gender = "Male";
            if(tempCourse.getChild().getChildGender() == 0){
                gender = "Female";
            }
            deleteTeacherCourseCardForTeacherBinding.childGenderTextView.setText(gender);
            if(tempCourse.getChild().getGrade() == 1){
                deleteTeacherCourseCardForTeacherBinding.childGradeTextView.setText(String.format("%dst Grade", tempCourse.getChild().getGrade()));
            }
            else if(tempCourse.getChild().getGrade() == 2){
                deleteTeacherCourseCardForTeacherBinding.childGradeTextView.setText(String.format("%dnd Grade", tempCourse.getChild().getGrade()));
            }
            else {
                deleteTeacherCourseCardForTeacherBinding.childGradeTextView.setText(String.format("%dth Grade", tempCourse.getChild().getGrade()));
            }

            deleteTeacherCourseCardForTeacherBinding.parentNameTextView.setText(String.format("%s %s", tempCourse.getParent().getFirstName(), tempCourse.getParent().getLastName()));
            deleteTeacherCourseCardForTeacherBinding.parentEmailTextView.setText(tempCourse.getParent().getEmail());

            StringBuilder str = new StringBuilder();
            List<String> phone = tempCourse.getParent().getPhoneNumbersList();
            for(int i = 0 ; i< phone.size() ; i++){
                if(i + 1 == phone.size()){
                    str.append(phone.get(i));
                }
                else {
                    str.append(phone.get(i)).append(" — ");
                }
            }
            deleteTeacherCourseCardForTeacherBinding.parentPhoneTextView.setText(str);
            deleteTeacherCourseCardForTeacherBinding.declineCourseDelete.setOnClickListener(v->{
                database.sendDeclineRemovingRequestNotificationToParent(tempCourse);
                MyAlertDialog.showDialogForDone(getContext(),"Request Declined","Request Declined, the parent will be notified that the course is not deleted ..");
                removeRequestForTeacherDialog.dismiss();
            });

            deleteTeacherCourseCardForTeacherBinding.acceptCourseDelete.setOnClickListener(w->{
                customConfirmAlterDialog(tempCourse,removeRequestForTeacherDialog);
            });
        }
    }

    private void customConfirmAlterDialog(Course course,Dialog removeRequestForTeacherDialog){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.custom_dialog_builder, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.warning_icon);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView errorTextView = view.findViewById(R.id.errorTextView);
        titleTextView.setText("Confirm ");
        errorTextView.setText("Are You Sure of deleting this course ?");
        errorTextView.setTextColor(getResources().getColor(R.color.black));
        builder.setView(view);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.removeCourseForParentAndTeacher2(course);
                MyAlertDialog.showDialogForDone(getContext(),"Course Deleted","The course deleted, its no longer available ..");
                database.getAllParentCourses(email,TeacherFragment.this);
                dialog.dismiss();
                removeRequestForTeacherDialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onCourseDeclined(int flag, JSONArray courseDeclined) {
        if(flag == 0){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"No Date","Unable to show the data , request may be deleted ..");
        }
        else if(flag == 1 && courseDeclined != null) {
            Course tempCourse = new Course();
            List<String> parentPhoneNumbersList = new ArrayList<>();
            try{
                JSONObject jsonObject=courseDeclined.getJSONObject(0);
                String source = jsonObject.getString("source");

                if(source.equalsIgnoreCase("teacherCourse")){
                    int courseId = jsonObject.getInt("courseId");
                    String teacherEmail = jsonObject.getString("teacherEmail");
                    String parentEmail = jsonObject.getString("parentEmail");
                    int teacherSentRequestId = jsonObject.getInt("teacherSentRequestId");
                    int parentSentRequestId = jsonObject.getInt("parentSentRequestId");
                    String courses = jsonObject.getString("courses");
                    String availabilityForJob = jsonObject.getString("availabilityForJob");
                    String location = jsonObject.getString("location");
                    String teachingMethod = jsonObject.getString("teachingMethod");
                    String startTime = jsonObject.getString("startTime");
                    String endTime = jsonObject.getString("endTime");
                    String startDate = jsonObject.getString("startDate");
                    String endDate = jsonObject.getString("endDate");
                    double price = jsonObject.getDouble("price");
                    int childId = jsonObject.getInt("childId");
                    String childName = jsonObject.getString("childName");
                    String childAge = jsonObject.getString("childAge");
                    String childGender = jsonObject.getString("childGender");
                    String childGrade = jsonObject.getString("childGrade");
                    String parentFirstName = jsonObject.getString("firstName");
                    parentFirstName = parentFirstName.substring(0,1).toUpperCase()+parentFirstName.substring(1).toLowerCase();
                    String parentLastName = jsonObject.getString("lastName");
                    parentLastName = parentLastName.substring(0,1).toUpperCase()+parentLastName.substring(1).toLowerCase();

                    String phoneNumbers = jsonObject.getString("phoneNumbers");
                    if (phoneNumbers.contains(",")) {
                        String[] splitPhoneNumbers = phoneNumbers.split(",");
                        parentPhoneNumbersList.addAll(Arrays.asList(splitPhoneNumbers));
                    }
                    else
                        parentPhoneNumbersList.add(phoneNumbers.trim());


                    tempCourse.setCourseId(courseId);
                    tempCourse.setTeacherEmail(teacherEmail);
                    tempCourse.setParentEmail(parentEmail);
                    tempCourse.setTeacherSentRequestId(teacherSentRequestId);
                    tempCourse.setParentSentRequestId(parentSentRequestId);
                    tempCourse.setCourses(courses);
                    tempCourse.setDays(availabilityForJob);
                    tempCourse.setLocation(location);
                    tempCourse.setTeachingMethod(teachingMethod);
                    tempCourse.setStartTime(startTime);
                    tempCourse.setEndTime(endTime);
                    tempCourse.setStartDate(startDate);
                    tempCourse.setEndDate(endDate);
                    tempCourse.setPrice(price);
                    tempCourse.setChildId(childId);
                    tempCourse.setChild(new Children(childId,childName,childAge,Integer.parseInt(childGender),Integer.parseInt(childGrade)));
                    tempCourse.setParent(new Parent(parentEmail,parentFirstName,parentLastName,parentPhoneNumbersList,0));
                    showDeclinedCourseForTeacher(tempCourse);
                }


                else if(source.equalsIgnoreCase("parentChildrenCourse")){
                    int courseId = jsonObject.getInt("courseId");
                    String teacherEmail = jsonObject.getString("teacherEmail");
                    String parentEmail = jsonObject.getString("parentEmail");
                    int teacherSentRequestId = jsonObject.getInt("teacherSentRequestId");
                    int parentSentRequestId = jsonObject.getInt("parentSentRequestId");
                    String courses = jsonObject.getString("courses");
                    String availabilityForJob = jsonObject.getString("choseDays");
                    String location = jsonObject.getString("location");
                    String teachingMethod = jsonObject.getString("teachingMethod");
                    String startTime = jsonObject.getString("startTime");
                    String endTime = jsonObject.getString("endTime");
                    String startDate = jsonObject.getString("startDate");
                    String endDate = jsonObject.getString("endDate");
                    double price = jsonObject.getDouble("price");
                    int childId = jsonObject.getInt("childId");
                    String childName = jsonObject.getString("childName");
                    String childAge = jsonObject.getString("childAge");
                    String childGender = jsonObject.getString("childGender");
                    String childGrade = jsonObject.getString("childGrade");
                    String parentFirstName = jsonObject.getString("firstName");
                    parentFirstName = parentFirstName.substring(0,1).toUpperCase()+parentFirstName.substring(1).toLowerCase();
                    String parentLastName = jsonObject.getString("lastName");
                    parentLastName = parentLastName.substring(0,1).toUpperCase()+parentLastName.substring(1).toLowerCase();

                    String phoneNumbers = jsonObject.getString("phoneNumbers");
                    if (phoneNumbers.contains(",")) {
                        String[] splitPhoneNumbers = phoneNumbers.split(",");
                        parentPhoneNumbersList.addAll(Arrays.asList(splitPhoneNumbers));
                    }
                    else
                        parentPhoneNumbersList.add(phoneNumbers.trim());


                    tempCourse.setCourseId(courseId);
                    tempCourse.setTeacherEmail(teacherEmail);
                    tempCourse.setParentEmail(parentEmail);
                    tempCourse.setTeacherSentRequestId(teacherSentRequestId);
                    tempCourse.setParentSentRequestId(parentSentRequestId);
                    tempCourse.setCourses(courses);
                    tempCourse.setDays(availabilityForJob);
                    tempCourse.setLocation(location);
                    tempCourse.setTeachingMethod(teachingMethod);
                    tempCourse.setStartTime(startTime);
                    tempCourse.setEndTime(endTime);
                    tempCourse.setStartDate(startDate);
                    tempCourse.setEndDate(endDate);
                    tempCourse.setPrice(price);
                    tempCourse.setChildId(childId);
                    tempCourse.setChild(new Children(childId,childName,childAge,Integer.parseInt(childGender),Integer.parseInt(childGrade)));
                    tempCourse.setParent(new Parent(parentEmail,parentFirstName,parentLastName,parentPhoneNumbersList,0));
                    showDeclinedCourseForTeacher(tempCourse);
                }
            }
            catch(JSONException e){
                throw new RuntimeException(e);
            }
        }
        else if(flag == -1){
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Error","Error fetching data , try again later.");
        }
        else {
            MyAlertDialog.showCustomAlertDialogLoginError(getContext(),"Connection Error","Error connecting to database , please check your network ..");
        }
    }

    private void showDeclinedCourseForTeacher(Course tempCourse){
        if(getContext() != null && tempCourse != null){
            Dialog declinedCourseDialog = new Dialog(getContext());
            CourseDeclinedNotificationForTeacherLayoutBinding courseDeclinedNotificationForTeacherLayoutBinding = CourseDeclinedNotificationForTeacherLayoutBinding.inflate(LayoutInflater.from(getContext()));
            declinedCourseDialog.setContentView(courseDeclinedNotificationForTeacherLayoutBinding.getRoot());
            declinedCourseDialog.setCancelable(false);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(Objects.requireNonNull(declinedCourseDialog.getWindow()).getAttributes());
            layoutParams.width = 1300;
            layoutParams.height = 2300;
            declinedCourseDialog.getWindow().setAttributes(layoutParams);
            declinedCourseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            if(declinedCourseDialog.getWindow() != null)
                declinedCourseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            declinedCourseDialog.show();

            courseDeclinedNotificationForTeacherLayoutBinding.closeImageView.setOnClickListener(c->{
                declinedCourseDialog.dismiss();
            });

            courseDeclinedNotificationForTeacherLayoutBinding.coursesTextView.setText(tempCourse.getCourses());
            courseDeclinedNotificationForTeacherLayoutBinding.dateTextView.setText(tempCourse.getStartDate()+" - "+tempCourse.getEndDate());
            courseDeclinedNotificationForTeacherLayoutBinding.timeTextView.setText(tempCourse.getStartTime() + " - "+tempCourse.getEndTime());
            courseDeclinedNotificationForTeacherLayoutBinding.choseDaysTextView.setText(tempCourse.getDays());
            courseDeclinedNotificationForTeacherLayoutBinding.teachingMethodTextView.setText(tempCourse.getTeachingMethod());
            courseDeclinedNotificationForTeacherLayoutBinding.childNameTextView.setText(tempCourse.getChild().getChildName());

            if(tempCourse.getChild().getChildAge().equalsIgnoreCase("1")){
                courseDeclinedNotificationForTeacherLayoutBinding.childAgeTextView.setText(String.format("%s Year", tempCourse.getChild().getChildAge()));
            }
            else {
                courseDeclinedNotificationForTeacherLayoutBinding.childAgeTextView.setText(String.format("%s Years", tempCourse.getChild().getChildAge()));
            }
            String gender = "Male";
            if(tempCourse.getChild().getChildGender() == 0){
                gender = "Female";
            }
            courseDeclinedNotificationForTeacherLayoutBinding.childGenderTextView.setText(gender);
            if(tempCourse.getChild().getGrade() == 1){
                courseDeclinedNotificationForTeacherLayoutBinding.childGradeTextView.setText(String.format("%dst Grade", tempCourse.getChild().getGrade()));
            }
            else if(tempCourse.getChild().getGrade() == 2){
                courseDeclinedNotificationForTeacherLayoutBinding.childGradeTextView.setText(String.format("%dnd Grade", tempCourse.getChild().getGrade()));
            }
            else if(tempCourse.getChild().getGrade() == 3){
                courseDeclinedNotificationForTeacherLayoutBinding.childGradeTextView.setText(String.format("%dd Grade", tempCourse.getChild().getGrade()));
            }
            else {
                courseDeclinedNotificationForTeacherLayoutBinding.childGradeTextView.setText(String.format("%dth Grade", tempCourse.getChild().getGrade()));
            }

            courseDeclinedNotificationForTeacherLayoutBinding.parentNameTextView.setText(tempCourse.getParent().getFirstName()+" "+tempCourse.getParent().getLastName());
            courseDeclinedNotificationForTeacherLayoutBinding.parentEmailTextView.setText(tempCourse.getParent().getEmail());
            StringBuilder str = new StringBuilder();
            List<String> phone = tempCourse.getParent().getPhoneNumbersList();
            for(int i = 0 ; i< phone.size() ; i++){
                if(i + 1 == phone.size()){
                    str.append(phone.get(i));
                }
                else {
                    str.append(phone.get(i)).append(" — ");
                }
            }
            courseDeclinedNotificationForTeacherLayoutBinding.parentPhoneTextView.setText(str);
        }
    }
}