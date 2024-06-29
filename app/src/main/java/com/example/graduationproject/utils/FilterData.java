package com.example.graduationproject.utils;

import android.util.Log;
import android.widget.Toast;

import com.example.graduationproject.models.Teacher;
import com.example.graduationproject.models.TeacherMatchModel;
import com.example.graduationproject.models.TeacherPostRequest;

import java.util.ArrayList;
import java.util.List;

public class FilterData {
    private List<TeacherMatchModel> mainDataToBeFiltered ;

    public FilterData(List<TeacherMatchModel> mainDataToBeFiltered){
        this.mainDataToBeFiltered=mainDataToBeFiltered;
    }

    public FilterData(){

    }
    public List<TeacherMatchModel> filterBasedOnLocation(List<TeacherMatchModel> listOfData,String location){
        List<TeacherMatchModel> filteredListBasedOnLocation = new ArrayList<>();
        for(int i=0;i<listOfData.size();i++){
            TeacherMatchModel temp = listOfData.get(i);
            if(temp.getLocation().toLowerCase().contains(location.toLowerCase())){
                filteredListBasedOnLocation.add(temp);
            }
        }
        return filteredListBasedOnLocation;
    }



    public List<TeacherMatchModel> filterBasedOnCourse(List<TeacherMatchModel> listOfData,String course){
        List<TeacherMatchModel> filteredListBasedOnLocation = new ArrayList<>();
        for(TeacherMatchModel tmm : listOfData){
            if(tmm.getCourses().toLowerCase().contains(course.toLowerCase())){
                filteredListBasedOnLocation.add(tmm);
            }
        }
        return filteredListBasedOnLocation;
    }




    public List<TeacherMatchModel> filterBasedOnLocationAndCourse(List<TeacherMatchModel> listOfData,String location,String course){
        List<TeacherMatchModel> filteredList = new ArrayList<>();
        for(int i=0;i<listOfData.size();i++){
            TeacherMatchModel temp = listOfData.get(i);
            if(temp.getCourses().toLowerCase().contains(course.toLowerCase().trim()) && temp.getLocation().contains(location.trim().toLowerCase())){
                filteredList.add(temp);
            }
        }
        return filteredList;
    }




    public List<TeacherMatchModel> filterBasedOnSelectedGenderList(List<TeacherMatchModel> listOfData,List<String> genderList){
        if(genderList != null && genderList.contains("Any"))
            return  listOfData;


        List<TeacherMatchModel> filteredListBasedOnGenderList = new ArrayList<>();
        if(genderList != null){
            for(TeacherMatchModel tmm : listOfData){
                for(String genderVal : genderList){
                    if(tmm.getCustomChildData().getGender() == 1 && genderVal.equalsIgnoreCase("Male")){
                        filteredListBasedOnGenderList.add(tmm);
                        break;
                    }
                    else if(tmm.getCustomChildData().getGender() == 0 && genderVal.equalsIgnoreCase("Female")){
                        filteredListBasedOnGenderList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredListBasedOnGenderList;
    }



    public List<TeacherMatchModel> filterBasedOnSelectedGradeList(List<TeacherMatchModel> listOfData,List<String> gradeList){
        if(gradeList!= null && gradeList.contains("Any")){
            return listOfData;
        }
        List<TeacherMatchModel> filteredListBasedOnGradeList = new ArrayList<>();
        if(gradeList != null ){
            for(TeacherMatchModel tmm : listOfData){
                for(String grade : gradeList){
                    if(tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                        filteredListBasedOnGradeList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredListBasedOnGradeList;
    }

    public List<TeacherMatchModel> filterBasedOnSelectedTeachingMethodList(List<TeacherMatchModel> listOfData,List<String> teachingMethodList){
        if(teachingMethodList != null && teachingMethodList.contains("Any"))
            return listOfData;

        List<TeacherMatchModel> filteredListBasedOnTeachingMethodList = new ArrayList<>();
        if(teachingMethodList != null){
            for(TeacherMatchModel tmm : listOfData){
                for(String teachingMethod : teachingMethodList){
                    if(tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                        filteredListBasedOnTeachingMethodList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredListBasedOnTeachingMethodList;
    }

    public List<TeacherMatchModel> filterBasedOnLocationNoListAndGenderList(List<TeacherMatchModel> listOfData , String location, List<String> genderList){
        if(location == null || genderList == null)
            return listOfData;
        if(genderList.contains("Any"))
            return listOfData;
        List<TeacherMatchModel> filteredList = new ArrayList<>();

        for(TeacherMatchModel tmm : listOfData){
            for(String gender : genderList){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                    if(tmm.getCustomChildData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                        filteredList.add(tmm);
                        break;
                    }
                    else if(tmm.getCustomChildData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                        filteredList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredList;
    }


    public List<TeacherMatchModel> filterBasedOnLocationNoListAndGradeList(List<TeacherMatchModel> listOfData , String location, List<String> gradeList){
        if(location == null || gradeList == null)
            return listOfData;

        List<TeacherMatchModel> filteredList = new ArrayList<>();

        if(gradeList.contains("Any"))
            return filterBasedOnLocation(listOfData,location);

        for(TeacherMatchModel tmm : listOfData){
            for(String grade : gradeList){
                if(tmm.getLocation().toLowerCase().contains(location.trim().toLowerCase()) && tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                    filteredList.add(tmm);
                    break;
                }
            }
        }
        return filteredList;
    }


    public List<TeacherMatchModel> filterBasedOnLocationNoListAndTeachingMethodList(List<TeacherMatchModel> listOfData , String location, List<String> teachingMethodList){
        if(location == null || teachingMethodList == null)
            return listOfData;
        if(teachingMethodList.contains("Any"))
            return listOfData;


        List<TeacherMatchModel> filteredList = new ArrayList<>();
        for(TeacherMatchModel tmm : listOfData){
            for(String teachingMethod : teachingMethodList){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                    filteredList.add(tmm);
                    break;
                }
            }
        }
        return filteredList;
    }

    public List<TeacherMatchModel> filterBasedOnLocationNoListCoursesNoListAndGenderList(List<TeacherMatchModel> listOfData,
                                                                                         String location,String courses,List<String> genderList){
        if(location == null || courses == null || genderList == null)
            return listOfData;
        List<TeacherMatchModel> filteredList = new ArrayList<>();

        if(genderList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) && tmm.getLocation().toLowerCase().contains(location.toLowerCase()))
                    filteredList.add(tmm);
            }
            return filteredList;
        }
        else {
            for(TeacherMatchModel tmm : listOfData){
                for(String gender : genderList){
                    if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) &&
                            tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                        if(tmm.getCustomChildData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                            filteredList.add(tmm);
                            break;
                        }
                        else if(tmm.getCustomChildData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            return filteredList;
        }
    }

    public List<TeacherMatchModel> filterBasedOnLocationNoListCoursesNoListAndGradeList(List<TeacherMatchModel> listOfData,
                                                                                        String location,String courses,List<String> gradeList){
        if(location == null || courses == null || gradeList == null)
            return listOfData;
        List<TeacherMatchModel> filteredList = new ArrayList<>();
        if(gradeList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) &&
                            tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                        filteredList.add(tmm);
                }
            }
            return filteredList;
        }
        else {
            for(TeacherMatchModel tmm : listOfData){
                for(String grade : gradeList){
                    if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) &&
                            tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) &&
                            tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                        filteredList.add(tmm);
                        break;
                    }
                }
            }
            return filteredList;
        }
    }

    public List<TeacherMatchModel> filterBasedOnLocationNoListCoursesNoListAndTeachingMethodList(List<TeacherMatchModel> listOfData,String location,String courses,List<String> teachingMethodList){
        if(location == null || courses == null || teachingMethodList == null)
            return listOfData;

        List<TeacherMatchModel> filteredList = new ArrayList<>();
        if(teachingMethodList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
               if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses)){
                   filteredList.add(tmm);
               }
            }
            return filteredList;
        }
        else {
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                    for(String teachingMethod : teachingMethodList){
                        if(tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            return filteredList;
        }
    }

    public List<TeacherMatchModel> filterBasedOnLocationNoListCoursesNoListGenderListAndGradeList(List<TeacherMatchModel> listOfData,String location,
                                                                                                  String courses,List<String> genderList,List<String> gradeList){
        if(location == null || courses == null || gradeList == null || genderList == null)
            return listOfData;

        for(String item : genderList){
            Log.d("123---------> "+item,"123---------> "+item);
        }
        for(String item : gradeList){
            Log.d("123---------> "+item,"123---------> "+item);
        }


        List<TeacherMatchModel> filteredList = new ArrayList<>();
        if(gradeList.contains("Any") && genderList.contains("Any")){
            Log.d("2-----any---------->","2-----any---------->");
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses.toLowerCase()))
                    filteredList.add(tmm);
            }
            return filteredList;
        }
        else if(gradeList.contains("Any") && !genderList.contains("Any")){
            Log.d("grade list any---------->","grade list any---------->---------->");
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses.toLowerCase()))
                    for(String gender : genderList){
                        if(tmm.getCustomChildData().getGender()==1 && gender.equalsIgnoreCase("Male")){
                            filteredList.add(tmm);
                            break;
                        }
                        else if(tmm.getCustomChildData().getGender()==0 && gender.equalsIgnoreCase("Female")){
                            filteredList.add(tmm);
                            break;
                        }
                    }
            }
            return filteredList;
        }
        else if(!gradeList.contains("Any") && genderList.contains("Any")){
            Log.d("gender list any---------->","gender list any---------->");

            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                    for(String grade : gradeList){
                        if(tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            return filteredList;
        }
        else {
            for(TeacherMatchModel tmm : listOfData){
                Log.d("No Any ---------->","No Any ---------->");

                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                    for(String gender : genderList){
                        if(tmm.getCustomChildData().getGender()==1 && gender.equalsIgnoreCase("Male")){
                            filteredList.add(tmm);
                            break;
                        }
                        else if(tmm.getCustomChildData().getGender()==0 && gender.equalsIgnoreCase("Female")){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            for(TeacherMatchModel tmm : filteredList){
                int flag = 0 ;
                for(String grade : gradeList){
                    if(tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                        flag = 1;
                        break;
                    }
                }
                if(flag == 1)
                    filteredList.remove(tmm);
            }
            return filteredList;
        }
    }


    public List<TeacherMatchModel> filterBasedOnLocationNoListCoursesNoListGenderListAndTeachingMethodList(List<TeacherMatchModel> listOfData,String location,String courses,List<String> genderList,List<String> teachingMethodList){
        if(location == null || courses == null || genderList == null || teachingMethodList == null)
            return listOfData;

        List<TeacherMatchModel> filteredList = new ArrayList<>();

        if(genderList.contains("Any") && teachingMethodList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                    if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses.toLowerCase()))
                        filteredList.add(tmm);
                }
                return filteredList;
        }

        else if(genderList.contains("Any") && !teachingMethodList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) && tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                    for(String teachingMethod : teachingMethodList){
                        if(tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            return filteredList;
        }

        else if(!genderList.contains("Any") && teachingMethodList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) && tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                    for(String gender : genderList){
                        if(tmm.getCustomChildData().getGender()==1 && gender.equalsIgnoreCase("Male")){
                            filteredList.add(tmm);
                            break;
                        }
                        else if(tmm.getCustomChildData().getGender()==0 && gender.equalsIgnoreCase("Female")){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            return filteredList;
        }

        else if(!genderList.contains("Any") && !teachingMethodList.contains("Any")) {
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) && tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                    for(String gender : genderList){
                        if(tmm.getCustomChildData().getGender()==1 && gender.equalsIgnoreCase("Male")){
                            filteredList.add(tmm);
                            break;
                        }
                        else if(tmm.getCustomChildData().getGender()==0 && gender.equalsIgnoreCase("Female")){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            for(TeacherMatchModel filter : filteredList){
                int tempFlag = 0 ;
                for(String teachingMethod : teachingMethodList){
                    if(filter.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                       tempFlag = 1;
                       break ;
                    }
                }
                if(tempFlag == 0)
                    filteredList.remove(filter);
            }
        }
        return filteredList;
    }

    public List<TeacherMatchModel> filterBasedOnLocationNoListCoursesNoListGradeListAndTeachingMethodList(List<TeacherMatchModel> listOfData,String location,String courses,List<String> gradeList,List<String> teachingMethodList){
        if(location == null || courses == null || gradeList == null || teachingMethodList == null)
            return listOfData;

        List<TeacherMatchModel> filteredList = new ArrayList<>();

        if(gradeList.contains("Any") && teachingMethodList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses.toLowerCase()))
                    filteredList.add(tmm);
            }
            return filteredList;
        }

        else if(gradeList.contains("Any") && !teachingMethodList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) && tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                    for(String teachingMethod : teachingMethodList){
                        if(tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            return filteredList;
        }

        else if(!gradeList.contains("Any") && teachingMethodList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) && tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                    for(String grade : gradeList){
                        if((tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade))){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            return filteredList;
        }

        else {
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) && tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                    for(String grade : gradeList){
                        if(tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            for(TeacherMatchModel filter : filteredList){
                int tempFlag = 0 ;
                for(String teachingMethod : teachingMethodList){
                    if(filter.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                        tempFlag = 1;
                        break ;
                    }
                }
                if(tempFlag == 0)
                    filteredList.remove(filter);
            }
        }
        return filteredList;

    }

    public  List<TeacherMatchModel> filterBasedOnCoursesNoListAndGenderList(List<TeacherMatchModel> listOfData,String courses,List<String> genderList){
        if(courses == null || genderList == null){
            return listOfData;
        }
        List<TeacherMatchModel> filteredList = new ArrayList<>();

        if(genderList.contains("Any")) {
            for (TeacherMatchModel tmm : listOfData) {
                if (tmm.getCourses().toLowerCase().contains(courses.toLowerCase())) {
                    filteredList.add(tmm);
                }
            }
            return filteredList;
        }
        for(TeacherMatchModel tmm : listOfData){
            if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                for(String gender : genderList){
                    if(tmm.getCustomChildData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                        filteredList.add(tmm);
                        break;
                    }
                    if(tmm.getCustomChildData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                        filteredList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredList;
    }


    public List<TeacherMatchModel> filterBasedOnCoursesNoListAndGrade(List<TeacherMatchModel> listOfData,String courses,List<String> gradeList){
        if(courses == null || gradeList == null){
            return listOfData;
        }
        List<TeacherMatchModel> filteredList = new ArrayList<>();

        if(gradeList.contains("Any")) {
            for (TeacherMatchModel tmm : listOfData) {
                if (tmm.getCourses().toLowerCase().contains(courses.toLowerCase())) {
                    filteredList.add(tmm);
                }
            }
            return filteredList;
        }
        for(TeacherMatchModel tmm : listOfData){
            if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                for(String grade : gradeList){
                    if(tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                        filteredList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredList;
    }

    public List<TeacherMatchModel>filterBasedOnCoursesNoListAndTeachingMethod(List<TeacherMatchModel> listOfData,String courses,List<String> teachingMethodList){
        if(courses == null || teachingMethodList == null){
            return listOfData;
        }
        List<TeacherMatchModel> filteredList = new ArrayList<>();

        if(teachingMethodList.contains("Any")) {
            for (TeacherMatchModel tmm : listOfData) {
                if (tmm.getCourses().toLowerCase().contains(courses.toLowerCase())) {
                    filteredList.add(tmm);
                }
            }
            return filteredList;
        }
        for(TeacherMatchModel tmm : listOfData){
            if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                for(String teachingMethod : teachingMethodList){
                    if(tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                        filteredList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredList;
    }

    public List<TeacherMatchModel> filterBasedOnCourseNoListAndGenderAndGrade(List<TeacherMatchModel> listOfData,String courses,List<String> genderList,List<String> gradeList){
        if(courses == null || genderList == null || gradeList == null)
            return listOfData;

        List<TeacherMatchModel> filteredList = new ArrayList<>();
        if(genderList.contains("Any") && gradeList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses))
                    filteredList.add(tmm);
            }
            return filteredList;
        }
        else if(genderList.contains("Any") && !gradeList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses))
                    for(String grade : gradeList){
                        if(tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                            filteredList.add(tmm);
                            break;
                        }
                    }
            }
            return filteredList;
        }

        else if(!genderList.contains("Any") && gradeList.contains("Any")){
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses))
                    for(String gender : genderList){
                        if(tmm.getCustomChildData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                            filteredList.add(tmm);
                            break;
                        }
                        if(tmm.getCustomChildData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                            filteredList.add(tmm);
                            break;
                        }
                    }
            }
            return filteredList;
        }
        else {
            for(TeacherMatchModel tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses))
                    for(String gender : genderList){
                        if(tmm.getCustomChildData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                            filteredList.add(tmm);
                            break;
                        }
                        if(tmm.getCustomChildData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                            filteredList.add(tmm);
                            break;
                        }
                    }
            }

            for(TeacherMatchModel tmm : filteredList){
                int flag = 0;
                for(String grade : gradeList){
                    if(tmm.getCustomChildData().getChildGrade() == Integer.parseInt(grade)){
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0)
                    filteredList.remove(tmm);
            }
            return filteredList;
        }
    }

    public List<TeacherMatchModel> filterBasedOnCourseNoListAndGenderGradeAndTeachingMethod(List<TeacherMatchModel> listOfData,String courses,List<String> genderList,List<String> gradeList,List<String> teachingMethodList){
        if(courses == null || genderList == null || teachingMethodList == null || gradeList == null)
            return listOfData;
        List<TeacherMatchModel> filteredList = filterBasedOnCourseNoListAndGenderAndGrade(listOfData,courses,genderList,gradeList);
        if(teachingMethodList.contains("Any")){
            return filteredList;
        }
        else {
            for(TeacherMatchModel tmm : filteredList){
                int flag = 0;
                for(String str : teachingMethodList){
                    if(tmm.getTeachingMethod().equalsIgnoreCase(str)){
                        flag = 1 ;
                        break;
                    }
                }
                if(flag == 0){
                    filteredList.remove(tmm);
                }
            }
        }
        return filteredList;
    }

    public List<TeacherMatchModel> filterBasedOnLocationNoListGenderAndGradeList(List<TeacherMatchModel> listOfData,String location,List<String> genderList,List<String> gradeList){
        if(location==null || genderList == null || gradeList == null)
            return listOfData;
        List<TeacherMatchModel> filteredLocationAndGrade = filterBasedOnLocationNoListAndGradeList(listOfData,location,gradeList);
        if(genderList.contains("Any")){
            return filteredLocationAndGrade;
        }
        for(TeacherMatchModel tmm : filteredLocationAndGrade){
            int flag = 0;
            for(String gender : genderList){
                if(tmm.getCustomChildData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                    flag = 0 ;
                    break;
                }
                if(tmm.getCustomChildData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                    flag = 0;
                    break;
                }
                flag = 1;
            }
            if(flag == 1){
                filteredLocationAndGrade.remove(tmm);
            }
        }
        return filteredLocationAndGrade;
    }

    public List<TeacherMatchModel> filterBasedOnLocationNoListGenderAndTeachingMethodList(List<TeacherMatchModel> listOfData,String location,List<String> genderList,List<String> teachingMethodList){
        if(location==null || genderList == null ||teachingMethodList == null )
            return listOfData;

        List<TeacherMatchModel> filteredLocationAndGender = filterBasedOnLocationNoListAndGenderList(listOfData,location,genderList);

        if(teachingMethodList.contains("Any"))
            return filteredLocationAndGender;

        for(TeacherMatchModel tmm : filteredLocationAndGender){
            int flag = 0;
            for(String teachingMethod : teachingMethodList){
                if(tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                    flag = 0;
                    break ;
                }
                flag = 1;
            }
            if(flag == 1){
                filteredLocationAndGender.remove(tmm);
            }
        }
        return filteredLocationAndGender;
    }


    public List<TeacherMatchModel> filterBasedOnLocationNoListGradeAndTeachingMethodList(List<TeacherMatchModel> listOfData,String location,List<String> gradeList,List<String> teachingMethodList){
        if(location==null || gradeList == null ||teachingMethodList == null )
            return listOfData;

        List<TeacherMatchModel> filteredLocationAndGrade = filterBasedOnLocationNoListAndGradeList(listOfData,location,gradeList);

        if(teachingMethodList.contains("Any"))
            return filteredLocationAndGrade;

        for(TeacherMatchModel tmm : filteredLocationAndGrade){
            int flag = 0;
            for(String teachingMethod : teachingMethodList){
                if(tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                    flag = 0;
                    break ;
                }
                flag = 1;
            }
            if(flag == 1){
                filteredLocationAndGrade.remove(tmm);
            }
        }
        return filteredLocationAndGrade;
    }

    public List<TeacherPostRequest> filterTeacherPostedBasedOnLocation(List<TeacherPostRequest> listOfData, String location){
        List<TeacherPostRequest> filteredListBasedOnLocation = new ArrayList<>();
        for(int i=0;i<listOfData.size();i++){
            TeacherPostRequest temp = listOfData.get(i);
            if(temp.getLocation().toLowerCase().contains(location.toLowerCase())){
                filteredListBasedOnLocation.add(temp);
            }
        }
        return filteredListBasedOnLocation;
    }

    public List<TeacherPostRequest> filterTeacherPostRequestBasedOnCourse(List<TeacherPostRequest> listOfData,String course){
        List<TeacherPostRequest> filteredListBasedOnLocation = new ArrayList<>();
        for(TeacherPostRequest tmm : listOfData){
            if(tmm.getCourses().toLowerCase().contains(course.toLowerCase())){
                filteredListBasedOnLocation.add(tmm);
            }
        }
        return filteredListBasedOnLocation;
    }

    public List<TeacherPostRequest> filterTeacherPostRequestBasedOnLocationAndCourse(List<TeacherPostRequest> listOfData,String location,String course){
        if(location==null || course == null)
            return listOfData;
        List<TeacherPostRequest> filteredList = new ArrayList<>();
        for(TeacherPostRequest tpr:listOfData){
            if(tpr.getCourses().toLowerCase().contains(course.trim().toLowerCase())&& tpr.getLocation().trim().contains(location.trim().toLowerCase())){
                filteredList.add(tpr);
            }
        }
        return filteredList;
    }


    public List<TeacherPostRequest> filterTeacherPostRequestBasedOnSelectedGenderList(List<TeacherPostRequest> listOfData,List<String> genderList){
        if(genderList != null && genderList.contains("Any"))
            return  listOfData;


        List<TeacherPostRequest> filteredListBasedOnGenderList = new ArrayList<>();
        if(genderList != null){
            for(TeacherPostRequest tmm : listOfData){
                for(String genderVal : genderList){
                    if(tmm.getTeacherData().getGender() == 1 && genderVal.equalsIgnoreCase("Male")){
                        filteredListBasedOnGenderList.add(tmm);
                        break;
                    }
                    else if(tmm.getTeacherData().getGender() == 0 && genderVal.equalsIgnoreCase("Female")){
                        filteredListBasedOnGenderList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredListBasedOnGenderList;
    }

    public List<TeacherPostRequest> filterTeacherPostRequestsBasedOnSelectedGradeList(List<TeacherPostRequest> listOfData,List<String> gradeList){
        if(gradeList == null || gradeList.contains("Any")){
            return listOfData;
        }

        List<TeacherPostRequest> filteredListBasedOnGradeList = new ArrayList<>();
            for(TeacherPostRequest tmm : listOfData){
                for(String grade : gradeList){
                    int gradeVal = Integer.parseInt(grade);
                    if(tmm.getTeacherData().getEducationalLevel().equalsIgnoreCase("Elementary School") && gradeVal > 0 && gradeVal <= 5){
                        filteredListBasedOnGradeList.add(tmm);
                        break;
                    }
                    else if(tmm.getTeacherData().getEducationalLevel().equalsIgnoreCase("Middle School") && gradeVal > 5 && gradeVal <= 10){
                        filteredListBasedOnGradeList.add(tmm);
                        break;
                    }
                    else if(tmm.getTeacherData().getEducationalLevel().equalsIgnoreCase("High School") && gradeVal > 10 && gradeVal <= 12){
                        filteredListBasedOnGradeList.add(tmm);
                        break;
                    }
                }
        }
        return filteredListBasedOnGradeList;
    }


}
