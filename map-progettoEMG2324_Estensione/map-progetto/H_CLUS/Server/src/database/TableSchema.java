package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe che rappresenta lo schema di una tabella di un database.
 *
 * Fornisce metodi per ottenere informazioni sulla struttura della tabella, inclusi
 * i nomi e i tipi degli attributi.
 */
public class TableSchema {
	/**
	 * Oggetto {@link DbAccess} utilizzato per gestire la connessione al database.
	 */
	private final DbAccess db;

	/**
	 * Classe interna che rappresenta un attributo della tabella.
	 */
	public static class Column{
		/** Nome dell'attributo della tabella */
		private final String name;
		/** Tipo dell'attributo della tabella */
		private final String type;

		/**
		 * Costruttore che inizializza un attributo della tabella con nome e tipo specificati.
		 *
		 * @param name Nome dell'attributo.
		 * @param type Tipo dell'attributo.
		 */
		Column(String name,String type){
			this.name=name;
			this.type=type;
		}

		/**
		 * Restituisce il nome dell'attributo.
		 *
		 * @return Nome dell'attributo.
		 */
		public String getColumnName(){
			return name;
		}

		/**
		 * Verifica se l'attributo è di tipo numerico.
		 *
		 * @return {@code true} se l'attributo è numerico, altrimenti {@code false}.
		 */
		public boolean isNumber(){
			return type.equals("number");
		}

		/**
		 * Restituisce una rappresentazione testuale dell'attributo.
		 *
		 * @return Stringa che rappresenta l'attributo nel formato "nome:tipo".
		 */
		public String toString(){
			return name+":"+type;
		}
	}

	/**
	 * Lista degli attributi della tabella.
	 */
	private List<Column> tableSchema= new ArrayList<>();

	/**
	 * Costruttore che recupera lo schema della tabella specificata.
	 *
	 * @param db Connessione al database.
	 * @param tableName Nome della tabella di cui ottenere lo schema.
	 * @throws SQLException Se si verifica un errore SQL durante il recupero delle informazioni.
	 * @throws DatabaseConnectionException Se si verifica un errore di connessione al database.
	 */
	public TableSchema(DbAccess db, String tableName) throws SQLException, DatabaseConnectionException{
		this.db=db;
		HashMap<String,String> mapSQL_JAVATypes= new HashMap<>();
		//http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/mapping.html
		mapSQL_JAVATypes.put("CHAR","string");
		mapSQL_JAVATypes.put("VARCHAR","string");
		mapSQL_JAVATypes.put("LONGVARCHAR","string");
		mapSQL_JAVATypes.put("BIT","string");
		mapSQL_JAVATypes.put("SHORT","number");
		mapSQL_JAVATypes.put("INT","number");
		mapSQL_JAVATypes.put("LONG","number");
		mapSQL_JAVATypes.put("FLOAT","number");
		mapSQL_JAVATypes.put("DOUBLE","number");

		Connection con=db.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet res = meta.getColumns(null, null, tableName, null);

		while (res.next()) {
			if(mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
				tableSchema.add(new Column(
						res.getString("COLUMN_NAME"),
						mapSQL_JAVATypes.get(res.getString("TYPE_NAME")))
				);
		}
		res.close();
	}

	/**
	 * Restituisce il numero di attributi presenti nella tabella.
	 *
	 * @return Numero di attributi della tabella.
	 */
	public int getNumberOfAttributes(){
		return tableSchema.size();
	}

	/**
	 * Restituisce l'attributo della tabella in base alla posizione specificata.
	 *
	 * @param index Indice dell'attributo da ottenere.
	 * @return Oggetto {@link Column} che rappresenta l'attributo.
	 */
	public Column getColumn(int index){
		return tableSchema.get(index);
	}

}




