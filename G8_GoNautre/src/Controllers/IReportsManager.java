package Controllers;

import java.util.ArrayList;

import logic.VisitReport;

public interface IReportsManager {
	
	public ArrayList<VisitReport> countSolosEnterTime(int month);
	public ArrayList<VisitReport> countSubsEnterTime(int month);
	public ArrayList<VisitReport> countGroupsEnterTime(int month);
	
	public ArrayList<VisitReport> countSolosEnterTimeWithDays(int month, String day);
	public ArrayList<VisitReport> countSubsEnterTimeWithDays(int month, String day);
	public ArrayList<VisitReport> countGroupsEnterTimeWithDays(int month, String day);
	
}
