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
    public List<TeacherPostRequest> filterBasedOnLocation(List<TeacherPostRequest> listOfData,String location){
        List<TeacherPostRequest> filteredListBasedOnLocation = new ArrayList<>();
        for(int i=0;i<listOfData.size();i++){
            TeacherPostRequest temp = listOfData.get(i);
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




    public List<TeacherPostRequest> filterBasedOnSelectedGenderList(List<TeacherPostRequest> listOfData,List<String> genderList){
        if(genderList == null)
            return  listOfData;

        if(genderList.contains("Any")){
            return listOfData;
        }


        List<TeacherPostRequest> filteredListBasedOnGenderList = new ArrayList<>();
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
        return filteredListBasedOnGenderList;
    }



    public List<TeacherPostRequest> filterBasedOnSelectedTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> teachingMethodList){
        if(teachingMethodList == null || teachingMethodList.contains("Any"))
            return listOfData;

        List<TeacherPostRequest> filteredListBasedOnTeachingMethodList = new ArrayList<>();
            for(TeacherPostRequest tmm : listOfData){
                for(String teachingMethod : teachingMethodList){
                    if(tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                        filteredListBasedOnTeachingMethodList.add(tmm);
                        break;
                    }
                }
            }
        return filteredListBasedOnTeachingMethodList;
    }

    public List<TeacherPostRequest> filterBasedOnLocationNoListAndGenderList(List<TeacherPostRequest> listOfData , String location, List<String> genderList){
        if(location == null || genderList == null)
            return listOfData;

        List<TeacherPostRequest> filteredList = new ArrayList<>();

        if(genderList.contains("Any")){
            for(TeacherPostRequest tpr : listOfData){
                if(tpr.getLocation().toLowerCase().contains(location.toLowerCase())){
                    filteredList.add(tpr);
                }
            }
            return filteredList;
        }

        for(TeacherPostRequest tmm : listOfData){
            for(String gender : genderList){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                    if(tmm.getTeacherData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                        filteredList.add(tmm);
                        break;
                    }
                    else if(tmm.getTeacherData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                        filteredList.add(tmm);
                        break;
                    }
                }
            }
        }
        return filteredList;
    }


    public List<TeacherPostRequest> filterBasedOnLocationNoListAndGradeList(List<TeacherPostRequest> listOfData , String location, List<String> gradeList){
        if(location == null || gradeList == null)
            return listOfData;

        if(gradeList.contains("Any"))
            return filterBasedOnLocation(listOfData,location);

        List<TeacherPostRequest> filteredList1 = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        List<TeacherPostRequest> filteredList2= new ArrayList<>();

        for(TeacherPostRequest tmm : filteredList1){
                if(tmm.getLocation().toLowerCase().contains(location.trim().toLowerCase())){
                    filteredList2.add(tmm);
                }
        }
        return filteredList2;
    }


    public List<TeacherPostRequest> filterBasedOnLocationNoListAndTeachingMethodList(List<TeacherPostRequest> listOfData , String location, List<String> teachingMethodList){
        if(location == null || teachingMethodList == null)
            return listOfData;
        if(teachingMethodList.contains("Any"))
            return listOfData;


        List<TeacherPostRequest> filteredList = new ArrayList<>();
        for(TeacherPostRequest tmm : listOfData){
            for(String teachingMethod : teachingMethodList){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getTeachingMethod().equalsIgnoreCase(teachingMethod)){
                    filteredList.add(tmm);
                    break;
                }
            }
        }
        return filteredList;
    }

    public List<TeacherPostRequest> filterBasedOnLocationNoListCoursesNoListAndGenderList(List<TeacherPostRequest> listOfData,
                                                                                         String location,String courses,List<String> genderList){
        if(location == null || courses == null || genderList == null)
            return listOfData;
        List<TeacherPostRequest> filteredList = new ArrayList<>();

        if(genderList.contains("Any")){
            for(TeacherPostRequest tmm : listOfData){
                if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) && tmm.getLocation().toLowerCase().contains(location.toLowerCase()))
                    filteredList.add(tmm);
            }
            return filteredList;
        }
        else {
            for(TeacherPostRequest tmm : listOfData){
                for(String gender : genderList){
                    if(tmm.getCourses().toLowerCase().contains(courses.toLowerCase()) &&
                            tmm.getLocation().toLowerCase().contains(location.toLowerCase())){
                        if(tmm.getTeacherData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                            filteredList.add(tmm);
                            break;
                        }
                        else if(tmm.getTeacherData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                            filteredList.add(tmm);
                            break;
                        }
                    }
                }
            }
            return filteredList;
        }
    }

    public List<TeacherPostRequest> filterBasedOnLocationNoListCoursesNoListAndGradeList(List<TeacherPostRequest> listOfData,
                                                                                        String location,String courses,List<String> gradeList){
        if(location == null || courses == null || gradeList == null)
            return listOfData;
        List<TeacherPostRequest> filteredList = new ArrayList<>();
        if(gradeList.contains("Any")){
            for(TeacherPostRequest tmm : listOfData){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) &&
                            tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                        filteredList.add(tmm);
                }
            }
            return filteredList;
        }
        else {
            List<TeacherPostRequest> gradeFilteredList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
            for(TeacherPostRequest tmm : gradeFilteredList){
                    if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) &&
                            tmm.getCourses().toLowerCase().contains(courses.toLowerCase())){
                        filteredList.add(tmm);
                    }
            }
            return filteredList;
        }
    }

    public List<TeacherPostRequest> filterBasedOnLocationNoListCoursesNoListAndTeachingMethodList(List<TeacherPostRequest> listOfData,String location,String courses,List<String> teachingMethodList){
        if(location == null || courses == null || teachingMethodList == null)
            return listOfData;

        List<TeacherPostRequest> filteredList = new ArrayList<>();
        if(teachingMethodList.contains("Any")){
            for(TeacherPostRequest tmm : listOfData){
               if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses)){
                   filteredList.add(tmm);
               }
            }
            return filteredList;
        }
        else {
            for(TeacherPostRequest tmm : listOfData){
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

    public List<TeacherPostRequest> filterBasedOnLocationNoListCoursesNoListGenderListAndGradeList(List<TeacherPostRequest> listOfData,String location,
                                                                                                  String courses,List<String> genderList,List<String> gradeList){
        if(location == null || courses == null || gradeList == null || genderList == null)
            return listOfData;



        List<TeacherPostRequest> filteredList = new ArrayList<>();
        if(gradeList.contains("Any") && genderList.contains("Any")){
            for(TeacherPostRequest tmm : listOfData){
                if(tmm.getLocation().toLowerCase().contains(location.toLowerCase()) && tmm.getCourses().toLowerCase().contains(courses.toLowerCase()))
                    filteredList.add(tmm);
            }
            return filteredList;
        }

        List<TeacherPostRequest> gradeFilteredList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        if(genderList.contains("Any")){
            for(TeacherPostRequest tpr : gradeFilteredList){
                if(tpr.getLocation().toLowerCase().contains(location.toLowerCase()) && tpr.getCourses().toLowerCase().contains(courses.toLowerCase()))
                    filteredList.add(tpr);
            }
            return filteredList;
        }

        for(TeacherPostRequest tpr : gradeFilteredList){
            if(tpr.getLocation().toLowerCase().contains(location.toLowerCase()) && tpr.getCourses().toLowerCase().contains(courses.toLowerCase())){
                for(String gender : genderList){
                    if(tpr.getTeacherData().getGender() == 1 && gender.equalsIgnoreCase("Male")){
                        filteredList.add(tpr);
                        break;
                    }
                    if (tpr.getTeacherData().getGender() == 0 && gender.equalsIgnoreCase("Female")){
                        filteredList.add(tpr);
                        break;
                    }
                }
            }
        }
        return filteredList;
    }


    public List<TeacherPostRequest> filterBasedOnLocationNoListCoursesNoListGenderListAndTeachingMethodList(List<TeacherPostRequest> listOfData,String location,String courses,List<String> genderList,List<String> teachingMethodList){
        if(location == null || courses == null || genderList == null || teachingMethodList == null)
            return listOfData;

        List<TeacherPostRequest> filteredList ;
        List<TeacherPostRequest> genderFilteredList = filterTeacherPostRequestBasedOnSelectedGenderList(listOfData,genderList);
        filteredList = filterTeacherPostRequestBasedOnLocationAndCourse(genderFilteredList,location,courses);
        return filterBasedOnSelectedTeachingMethodList(filteredList,teachingMethodList);
    }




    public List<TeacherPostRequest> filterBasedOnLocationNoListCoursesNoListGradeListAndTeachingMethodList(List<TeacherPostRequest> listOfData,String location,String courses,List<String> gradeList,List<String> teachingMethodList){
        if(location == null || courses == null || gradeList == null || teachingMethodList == null)
            return listOfData;

        List<TeacherPostRequest> filteredList ;
        List<TeacherPostRequest> gradeFilteredList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        filteredList = filterTeacherPostRequestBasedOnLocationAndCourse(gradeFilteredList,location,courses);
        if(teachingMethodList.contains("Any"))
            return filteredList;
        return filterBasedOnSelectedTeachingMethodList(filteredList,teachingMethodList);
    }





    public  List<TeacherPostRequest> filterBasedOnCoursesNoListAndGenderList(List<TeacherPostRequest> listOfData,String courses,List<String> genderList){
        if(courses == null || genderList == null){
            return listOfData;
        }
        List<TeacherPostRequest> filteredList = filterTeacherPostRequestBasedOnSelectedGenderList(listOfData,genderList);
        return filterTeacherPostRequestBasedOnCourse(filteredList,courses);
    }




    public List<TeacherPostRequest> filterBasedOnCoursesNoListAndGrade(List<TeacherPostRequest> listOfData,String courses,List<String> gradeList){
        if(courses == null || gradeList == null){
            return listOfData;
        }
        List<TeacherPostRequest> filteredList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        return filterTeacherPostRequestBasedOnCourse(filteredList,courses);

    }






    public List<TeacherPostRequest>filterBasedOnCoursesNoListAndTeachingMethod(List<TeacherPostRequest> listOfData,String courses,List<String> teachingMethodList){
        if(courses == null || teachingMethodList == null){
            return listOfData;
        }
        List<TeacherPostRequest> filteredList = filterBasedOnSelectedTeachingMethodList(listOfData,teachingMethodList);
        return filterTeacherPostRequestBasedOnCourse(filteredList,courses);
    }





    public List<TeacherPostRequest> filterBasedOnCourseNoListAndGenderAndGrade(List<TeacherPostRequest> listOfData,String courses,List<String> genderList,List<String> gradeList){
        if(courses == null || genderList == null || gradeList == null)
            return listOfData;

        List<TeacherPostRequest> gradeFilteredList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        List<TeacherPostRequest> genderFilteredList = filterTeacherPostRequestBasedOnSelectedGenderList(gradeFilteredList,gradeList);
        return filterTeacherPostRequestBasedOnCourse(genderFilteredList,courses);
    }



    public List<TeacherPostRequest> filterBasedOnCourseNoListAndGenderGradeAndTeachingMethod(List<TeacherPostRequest> listOfData,String courses,List<String> genderList,List<String> gradeList,List<String> teachingMethodList){
        if(courses == null || genderList == null || teachingMethodList == null || gradeList == null)
            return listOfData;

        List<TeacherPostRequest> gradeFilteredList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        List<TeacherPostRequest> genderFilteredList = filterTeacherPostRequestBasedOnSelectedGenderList(gradeFilteredList,genderList);
        List<TeacherPostRequest> teachingMethodFilteredList = filterBasedOnSelectedTeachingMethodList(genderFilteredList,teachingMethodList);
        return filterTeacherPostRequestBasedOnCourse(teachingMethodFilteredList,courses);

    }

    public List<TeacherPostRequest> filterBasedOnLocationNoListGenderAndGradeList(List<TeacherPostRequest> listOfData,String location,List<String> genderList,List<String> gradeList){
        if(location==null || genderList == null || gradeList == null)
            return listOfData;
        List<TeacherPostRequest> gradeFilteredList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        List<TeacherPostRequest> genderFilteredList = filterTeacherPostRequestBasedOnSelectedGenderList(gradeFilteredList,genderList);
        return filterTeacherPostedBasedOnLocation(genderFilteredList,location);
    }



    public List<TeacherPostRequest> filterBasedOnLocationNoListGenderAndTeachingMethodList(List<TeacherPostRequest> listOfData,String location,List<String> genderList,List<String> teachingMethodList){
        if(location==null || genderList == null ||teachingMethodList == null )
            return listOfData;

        List<TeacherPostRequest> genderFilteredList = filterTeacherPostRequestBasedOnSelectedGenderList(listOfData,genderList);
        List<TeacherPostRequest> teachingMethodFilteredList = filterBasedOnSelectedTeachingMethodList(genderFilteredList,teachingMethodList);
        return filterTeacherPostedBasedOnLocation(teachingMethodFilteredList,location);

    }

    public List<TeacherPostRequest> filterTeacherBasedOnLocationNoListGenderGradeTeachingMethodList(List<TeacherPostRequest> listOfData, String location, List<String> genderList,
                                                                                                                       List<String> gradeList, List<String> teachingMethodList){
        if (location == null || genderList == null || gradeList == null || teachingMethodList==null)
            return listOfData;

        List<TeacherPostRequest> locationGradeGenderFilteredList = filterBasedOnLocationNoListGenderAndGradeList(listOfData,location,genderList,gradeList);
        return filterBasedOnSelectedTeachingMethodList(locationGradeGenderFilteredList,teachingMethodList);
    }




    public List<TeacherPostRequest> filterTeacherPostRequestsBasedOnLocationCoursesNoListGenderGradeTeachingMethodList(List<TeacherPostRequest> listOfData, String location, String courses, List<String> genderList,
                                                                                                                       List<String> gradeList, List<String> teachingMethodList){
        if (location == null || courses == null || genderList == null || gradeList == null || teachingMethodList==null)
            return listOfData;

        List<TeacherPostRequest> locationGradeGenderFilteredList = filterBasedOnLocationNoListGenderAndGradeList(listOfData,location,genderList,gradeList);
        return filterBasedOnCoursesNoListAndTeachingMethod(locationGradeGenderFilteredList,courses,teachingMethodList);
    }




    public List<TeacherPostRequest> filterBasedOnLocationNoListGradeAndTeachingMethodList(List<TeacherPostRequest> listOfData,String location,List<String> gradeList,List<String> teachingMethodList){
        if(location==null || gradeList == null ||teachingMethodList == null )
            return listOfData;

        List<TeacherPostRequest> filteredLocationAndGrade = filterBasedOnLocationNoListAndGradeList(listOfData,location,gradeList);
        return filterBasedOnSelectedTeachingMethodList(filteredLocationAndGrade,teachingMethodList);

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
        for(TeacherPostRequest tpr : listOfData){
            if(tpr.getLocation().toLowerCase().contains(location.toLowerCase()) && tpr.getCourses().toLowerCase().contains(course.toLowerCase()))
                filteredList.add(tpr);
        }
        return filteredList;
    }




    public List<TeacherPostRequest> filterTeacherPostRequestBasedOnSelectedGenderList(List<TeacherPostRequest> listOfData,List<String> genderList){
        if(genderList == null || genderList.contains("Any"))
            return  listOfData;


        List<TeacherPostRequest> filteredListBasedOnGenderList = new ArrayList<>();
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

    public List<TeacherPostRequest> filterBasedOnLocationList(List<TeacherPostRequest> listOfData,List<String> locationList){
        if(locationList == null)
            return listOfData;
        if(locationList.contains("Any"))
            return listOfData;

        for(String tpr : locationList)
            Log.d("Loaction is ------------> "+tpr.length(),"Loaction is ------------> "+tpr.length());

        Log.d("----------------","-------------------------");
        for(TeacherPostRequest tpr : listOfData)
            Log.d("Loaction is ------------> "+tpr.getLocation().length(),"Loaction is ------------> "+tpr.getLocation().length());


        List<TeacherPostRequest> filteredList = new ArrayList<>();
        for(TeacherPostRequest tpr : listOfData){
            for(String location : locationList){
                if(tpr.getLocation().toLowerCase().contains(location.toLowerCase())){
                    filteredList.add(tpr);
                    break;
                }
            }
        }
        return filteredList;
    }

    public List<TeacherPostRequest> filterBasedOnCoursesList(List<TeacherPostRequest> listOfData,List<String> coursesList){
        if(coursesList == null)
            return listOfData;
        if(coursesList.contains("Any"))
            return listOfData;

        List<TeacherPostRequest> filteredList = new ArrayList<>();
        for(TeacherPostRequest tpr : listOfData){
            for(String course : coursesList){
                if(tpr.getCourses().toLowerCase().contains(course.toLowerCase())){
                    filteredList.add(tpr);
                    break;
                }
            }
        }
        return filteredList;
    }


    public List<TeacherPostRequest> filterBasedOnLocationListAndCoursesList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> coursesList){
        if(coursesList == null || locationList == null)
            return listOfData;
        List<TeacherPostRequest> filteredLocationList = filterBasedOnLocationList(listOfData,locationList);
        return filterBasedOnCoursesList(filteredLocationList,coursesList);
    }


    public List<TeacherPostRequest> filterBasedOnLocationListAndGenderList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> genderList){
        if(genderList == null || locationList == null)
            return listOfData;
        List<TeacherPostRequest> filteredGenderList = filterTeacherPostRequestBasedOnSelectedGenderList(listOfData,genderList);
        return filterBasedOnLocationList(filteredGenderList,locationList);
    }



    public List<TeacherPostRequest> filterBasedOnLocationListAndGradeList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> gradeList){
        if(gradeList == null || locationList == null)
            return listOfData;
        List<TeacherPostRequest> filteredGradeList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        return filterBasedOnLocationList(filteredGradeList,locationList);
    }



    public List<TeacherPostRequest> filterBasedOnLocationListAndTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> teachingMethodList){
        if(teachingMethodList == null || locationList == null)
            return listOfData;
        List<TeacherPostRequest> filteredteachingMethodList = filterBasedOnSelectedTeachingMethodList(listOfData,teachingMethodList);
        return filterBasedOnLocationList(filteredteachingMethodList,locationList);
    }



    public List<TeacherPostRequest> filterBasedOnLocationListCoursesListAndGenderList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> coursesList,List<String> genderList){
        if(coursesList == null || locationList == null || genderList == null)
            return listOfData;
        List<TeacherPostRequest> locationAndCoursesFilteredList = filterBasedOnLocationListAndCoursesList(listOfData,locationList,coursesList);
        return filterBasedOnSelectedGenderList(locationAndCoursesFilteredList,genderList);
    }



    public List<TeacherPostRequest> filterBasedOnLocationListCoursesListAndGradeList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> coursesList,List<String> gradeList){
        if(coursesList == null || locationList == null || gradeList == null)
            return listOfData;
        List<TeacherPostRequest> locationAndCoursesFilteredList = filterBasedOnLocationListAndCoursesList(listOfData,locationList,coursesList);
        return filterTeacherPostRequestsBasedOnSelectedGradeList(locationAndCoursesFilteredList,gradeList);
    }

    public List<TeacherPostRequest> filterBasedOnLocationListCoursesListAndTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> coursesList,List<String> teachingMethodList){
        if(coursesList == null || locationList == null || teachingMethodList == null)
            return listOfData;
        List<TeacherPostRequest> locationAndCoursesFilteredList = filterBasedOnLocationListAndCoursesList(listOfData,locationList,coursesList);
        return filterBasedOnSelectedTeachingMethodList(locationAndCoursesFilteredList,teachingMethodList);
    }



    public List<TeacherPostRequest> filterBasedOnLocationListCoursesListGenderListAndGradeList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> coursesList,List<String> genderList,List<String> gradeList){
        if(coursesList == null || locationList == null || gradeList == null || genderList == null)
            return listOfData;
        List<TeacherPostRequest> locationCoursesGradeFilteredList = filterBasedOnLocationListCoursesListAndGradeList(listOfData,locationList,coursesList,gradeList);
        return filterBasedOnSelectedGenderList(locationCoursesGradeFilteredList,genderList);
    }

    public List<TeacherPostRequest> filterBasedOnLocationListCoursesListGenderListAndTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> coursesList,List<String> genderList,List<String> teachingMethod){
        if(coursesList == null || locationList == null || teachingMethod == null || genderList == null)
            return listOfData;
        List<TeacherPostRequest> locationCoursesGradeFilteredList = filterBasedOnLocationListCoursesListAndGenderList(listOfData,locationList,coursesList,genderList);
        return filterBasedOnSelectedTeachingMethodList(locationCoursesGradeFilteredList,teachingMethod);
    }

    public List<TeacherPostRequest> filterBasedOnAllList(List<TeacherPostRequest> listOfData,List<String> locationList,List<String> coursesList,List<String> genderList,List<String> gradeList,List<String> teachingMethod){
        if(coursesList == null || locationList == null || teachingMethod == null || genderList == null || gradeList == null)
            return listOfData;
        List<TeacherPostRequest> locationCoursesGenderGradeFilteredList = filterBasedOnLocationListCoursesListGenderListAndGradeList(listOfData,locationList,coursesList,genderList,gradeList);
        return filterBasedOnSelectedTeachingMethodList(locationCoursesGenderGradeFilteredList,teachingMethod);
    }

    public List<TeacherPostRequest> filterBasedCoursesAndGenderList(List<TeacherPostRequest> listOfData,List<String> coursesList,List<String> genderList){
        if(coursesList == null  || genderList == null)
            return listOfData;
        List<TeacherPostRequest> coursesFilteredList = filterBasedOnCoursesList(listOfData,coursesList);
        return filterBasedOnSelectedGenderList(coursesFilteredList,genderList);
    }

    public List<TeacherPostRequest> filterBasedCoursesAndGradeList(List<TeacherPostRequest> listOfData,List<String> coursesList,List<String> gradeList){
        if(coursesList == null  || gradeList == null)
            return listOfData;
        List<TeacherPostRequest> coursesFilteredList = filterBasedOnCoursesList(listOfData,coursesList);
        return filterTeacherPostRequestsBasedOnSelectedGradeList(coursesFilteredList,gradeList);
    }

    public List<TeacherPostRequest> filterBasedCoursesAndTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> coursesList,List<String> teachingMethod){
        if(coursesList == null  || teachingMethod == null)
            return listOfData;
        List<TeacherPostRequest> coursesFilteredList = filterBasedOnCoursesList(listOfData,coursesList);
        return filterBasedOnSelectedTeachingMethodList(coursesFilteredList,teachingMethod);
    }

    public List<TeacherPostRequest> filterBasedCoursesAndGenderListAndGradeList(List<TeacherPostRequest> listOfData,List<String> coursesList,List<String> genderList,List<String> gradeList){
        if(coursesList == null  || genderList == null || gradeList == null)
            return listOfData;
        List<TeacherPostRequest> coursesGradeFilteredList = filterBasedCoursesAndGradeList(listOfData,coursesList,gradeList);
        return filterBasedOnSelectedGenderList(coursesGradeFilteredList,genderList);
    }

    public List<TeacherPostRequest> filterBasedCoursesAndGenderListAndTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> coursesList,List<String> genderList,List<String> teachingMethodList){
        if(coursesList == null  || genderList == null || teachingMethodList == null)
            return listOfData;
        List<TeacherPostRequest> coursesGenderFilteredList = filterBasedCoursesAndGenderList(listOfData,coursesList,genderList);
        return filterBasedOnSelectedTeachingMethodList(coursesGenderFilteredList,teachingMethodList);
    }

    public List<TeacherPostRequest> filterBasedCoursesAndGenderListGradeAndTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> coursesList,List<String> genderList,List<String> gradeList,List<String> teachingMethodList){
        if(coursesList == null  || genderList == null || gradeList == null|| teachingMethodList == null)
            return listOfData;
        List<TeacherPostRequest> coursesGenderGradeFilteredList = filterBasedCoursesAndGenderListAndGradeList(listOfData,coursesList,genderList,gradeList);
        return filterBasedOnSelectedTeachingMethodList(coursesGenderGradeFilteredList,teachingMethodList);
    }

    public List<TeacherPostRequest> filterBasedOnGenderListGradeList(List<TeacherPostRequest> listOfData,List<String> genderList,List<String> gradeList){
        if(genderList == null || gradeList == null)
            return listOfData;
        List<TeacherPostRequest> genderFilteredList = filterBasedOnSelectedGenderList(listOfData,genderList);
        return filterTeacherPostRequestsBasedOnSelectedGradeList(genderFilteredList,gradeList);
    }

    public List<TeacherPostRequest> filterBasedOnGenderListTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> genderList,List<String> teachingMethod){
        if(genderList == null || teachingMethod == null)
            return listOfData;
        List<TeacherPostRequest> genderFilteredList = filterBasedOnSelectedGenderList(listOfData,genderList);
        return filterBasedOnSelectedTeachingMethodList(genderFilteredList,teachingMethod);
    }

    public List<TeacherPostRequest> filterBasedOnGenderListGradeTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> genderList,List<String> gradeList,List<String> teachingMethod){
        if(genderList == null || teachingMethod == null)
            return listOfData;
        List<TeacherPostRequest> gradeGenderFilteredList = filterBasedOnGenderListGradeList(listOfData,genderList,gradeList);
        return filterBasedOnSelectedTeachingMethodList(gradeGenderFilteredList,teachingMethod);
    }

    public List<TeacherPostRequest> filterBasedOnGradeTeachingMethodList(List<TeacherPostRequest> listOfData,List<String> gradeList,List<String> teachingMethod){
        if(gradeList == null || teachingMethod == null)
            return listOfData;
        List<TeacherPostRequest> gradeFilteredList = filterTeacherPostRequestsBasedOnSelectedGradeList(listOfData,gradeList);
        return filterBasedOnSelectedTeachingMethodList(gradeFilteredList,teachingMethod);
    }














}
