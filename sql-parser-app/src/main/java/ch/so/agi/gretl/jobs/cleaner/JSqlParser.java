package ch.so.agi.gretl.jobs.cleaner;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class JSqlParser {
    public List getTableList(String sql) throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse(sql);
//        Select selectStatement = (Select) stmt;
//        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
//        List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
//        return tableList;
        return new ArrayList();
    }
}