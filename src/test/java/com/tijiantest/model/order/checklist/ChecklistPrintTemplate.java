package com.tijiantest.model.order.checklist;

public class ChecklistPrintTemplate {
	private Integer id;
	private Integer hospitalId;//体检中心Id
	private String barcodePrintNote;//小票页体检须知
	private String pdfPrintNote;//pdf页体检须知
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}
	public String getBarcodePrintNote() {
		return barcodePrintNote;
	}
	public void setBarcodePrintNote(String barcodePrintNote) {
		this.barcodePrintNote = barcodePrintNote;
	}
	public String getPdfPrintNote() {
		return pdfPrintNote;
	}
	public void setPdfPrintNote(String pdfPrintNote) {
		this.pdfPrintNote = pdfPrintNote;
	}
	
}
