package org.unitime.timetable.solver.exam.ui;

import java.io.Serializable;

import org.unitime.timetable.ApplicationProperties;
import org.unitime.timetable.model.Exam;
import org.unitime.timetable.model.Location;
import org.unitime.timetable.model.PreferenceLevel;
import org.unitime.timetable.model.dao.LocationDAO;

import net.sf.cpsolver.exam.model.ExamRoom;
import net.sf.cpsolver.ifs.util.DistanceMetric;

public class ExamRoomInfo implements Serializable, Comparable<ExamRoomInfo>{
	private static final long serialVersionUID = -5882156641099610154L;
	private Long iId = null;
    private String iName = null;
    private int iPreference = 0;
    private int iCapacity, iExamCapacity = 0;
    private Double iX = null, iY = null;
    private transient Location iLocation = null;
    private transient static DistanceMetric sDistanceMetric = null;
    
    public ExamRoomInfo(ExamRoom room, int preference) {
        iId = room.getId();
        iName = room.getName();
        iCapacity = room.getSize();
        iExamCapacity = room.getAltSize();
        iPreference = preference;
        iX = room.getCoordX(); iY = room.getCoordY();
    }
    
    public ExamRoomInfo(Location location, int preference) {
        iLocation = location;
        iId = location.getUniqueId();
        iName = location.getLabel();
        iCapacity = location.getCapacity();
        iExamCapacity = (location.getExamCapacity()==null?location.getCapacity()/2:location.getExamCapacity());
        iPreference = preference;
        iX = location.getCoordinateX();
        iY = location.getCoordinateY();
    }
    
    public Long getLocationId() { return iId; }
    public String getName() { return iName; }
    public int getPreference() { return iPreference; }
    public void setPreference(int preference) { iPreference = preference; }
    public int getCapacity() { return iCapacity; }
    public int getExamCapacity() { return iExamCapacity; }
    public int getCapacity(ExamInfo exam) { return (exam.getSeatingType()==Exam.sSeatingTypeExam?getExamCapacity():getCapacity());}
    public Location getLocation() {
        if (iLocation==null) iLocation = new LocationDAO().get(getLocationId());
        return iLocation;
    }
    public Location getLocation(org.hibernate.Session hibSession) {
        return new LocationDAO().get(getLocationId(), hibSession);
    }
    
    
    public String toString() {
        return "<span style='color:"+PreferenceLevel.prolog2color(PreferenceLevel.int2prolog(getPreference()))+";' " +
        		"title='"+PreferenceLevel.prolog2string(PreferenceLevel.int2prolog(getPreference()))+" "+getName()+
        		" ("+getCapacity()+" seats, "+getExamCapacity()+" exam seats)'>"+getName()+"</span>";
    }
    
    public int compareTo(ExamRoomInfo room) {
        int cmp = -Double.compare(getCapacity(), room.getCapacity());
        if (cmp!=0) return cmp;
        cmp = getName().compareTo(room.getName());
        if (cmp!=0) return cmp;
        return getLocationId().compareTo(room.getLocationId());
    }
    
    public boolean equals(Object o) {
        if (o==null || !(o instanceof ExamRoomInfo)) return false;
        return getLocationId().equals(((ExamRoomInfo)o).getLocationId());
    }
    
    public int hashCode() {
        return getLocationId().hashCode();
    }
    
    public Double getCoordX() { return iX; }
    public Double getCoordY() { return iY; }
    
    public double getDistance(ExamRoomInfo other) {
    	if (sDistanceMetric == null) {
    		sDistanceMetric = new DistanceMetric(
    				DistanceMetric.Ellipsoid.valueOf(ApplicationProperties.getProperty("unitime.distance.ellipsoid", DistanceMetric.Ellipsoid.LEGACY.name())));
    	}
    	return sDistanceMetric.getDistanceInMeters(getCoordX(), getCoordY(), other.getCoordX(), other.getCoordY());
    }

}