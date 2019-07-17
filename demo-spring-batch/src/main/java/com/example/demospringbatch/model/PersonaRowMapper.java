package com.example.demospringbatch.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PersonaRowMapper implements RowMapper<Persona>{

	  @Override
	  public Persona mapRow(ResultSet rs, int rowNum) throws SQLException {
	Persona user = new Persona();
	   user.setPrimerNombre(rs.getString("primer_Nombre"));
	   user.setSegundoNombre(rs.getString("segundo_Nombre"));
	   user.setTelefono(rs.getString("telefono"));
	   
	   return user;
	  }
	  
	 }
	 