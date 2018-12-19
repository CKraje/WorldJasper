package it.objectmethod.worldjasper.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import it.objectmethod.worldjasper.model.Country;
import it.objectmethod.worldjasper.repository.CountryRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
public class CountryController {

	@Autowired
	CountryRepository countryRepo;

	@Autowired
	DataSource dataSource;
	

	@RequestMapping("/home")
	@ResponseBody
	public void testJasperReport(HttpServletResponse response,HttpServletRequest req)
			throws JRException, IOException, SQLException, ClassNotFoundException {

		Map<String, Object> params = new HashMap<String, Object>();
		String fileName= "report1_modified.jasper";
		JasperPrint jprint = getJasperPrint(params,fileName);
		String noExtensionFileName = fileName.
				substring(0,fileName.indexOf(".jasper"));
		exportPdfToResponse(jprint,noExtensionFileName,response);
	}

	@GetMapping("/{continent}/countries-by-continent")
	public void jasperReportCountryByContinent(HttpServletResponse response,
			@PathVariable("continent") String continent) 
					throws SQLException, JRException, IOException {

		Map<String, Object> params = new HashMap<String,Object>();
		params.put("cont", continent);
		String fileName= "countries_by_continent.jasper";
		JasperPrint jprint = getJasperPrint(params,fileName);
		String noExtensionFileName = fileName.
				substring(0,fileName.indexOf(".jasper"));
		exportPdfToResponse(jprint,noExtensionFileName,response);
	}

	@GetMapping("/{countrycode}/cities-by-country")
	public void jasperReportCityByCountry(HttpServletResponse response,
			@PathVariable("countrycode") String countryCode) throws SQLException, IOException, JRException {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("countryCode", countryCode);
		Country countryByCode= countryRepo.findByCode(countryCode);
		String countryName = countryByCode.getName();
		params.put("country", countryName);
		String fileName= "city_by_country.jasper";
		JasperPrint jprint = getJasperPrint(params,fileName);
		String noExtensionFileName = fileName.
				substring(0,fileName.indexOf(".jasper"));
		exportPdfToResponse(jprint,noExtensionFileName,response);
	}


	public void exportPdfToResponse(JasperPrint jprint, String filename, HttpServletResponse response)
			throws IOException, JRException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jprint, byteArrayOutputStream);
		sendOutputStreamToResponse(filename + ".pdf", "application/pdf", response, byteArrayOutputStream);
	}

	private void sendOutputStreamToResponse(String filename, String mimeType, HttpServletResponse response,
			ByteArrayOutputStream byteArrayOutputStream) throws IOException {
		response.setContentType(mimeType);
		ServletOutputStream outputstream = response.getOutputStream();
		byte[] out = byteArrayOutputStream.toByteArray();

		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.setHeader("Content-Length", String.valueOf(out.length));
		outputstream.write(out);
		outputstream.flush();
		outputstream.close();
	}

	public JasperPrint getJasperPrint(Map<String, Object> params,
			String jasperFileName) throws SQLException {
		Connection conn = dataSource.getConnection();
		JasperPrint jprint = null;
		try {
			InputStream jasperFile = this.getClass().getClassLoader().
					getResourceAsStream(jasperFileName);
			jprint = (JasperPrint) JasperFillManager.fillReport(jasperFile,
					params,conn);
		} catch (JRException e) {
			e.printStackTrace();
		} 
		finally {
			conn.close();
		}
		return jprint;

	}
}
