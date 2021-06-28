package csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVs {
    /**
     * @param isFirstLineHeader csv 파일의 첫 라인을 헤더(타이틀)로 처리할까요?
     */
    public static Table createTable(File csv, boolean isFirstLineHeader) throws FileNotFoundException {
        Scanner scanner = new Scanner(csv);
        TableImpl table = new TableImpl();
        List <ColumnImpl> columnList = new ArrayList<>();
        String[] headerArr = scanner.nextLine().split(",");

        if(isFirstLineHeader) {
            //isFirstLineHeader가 true면, Header가 들어간 headerArr 배열을 첫 라인에 넣는다.
            for(int i=0; i< headerArr.length; i++){
                ColumnImpl columnImpl = new ColumnImpl(headerArr[i]);
                columnList.add(columnImpl);
            }
            while(scanner.hasNext()){
                String tpString = scanner.nextLine();
                if(tpString.endsWith(","))
                   tpString = tpString.concat("null");
                String []tempArr = tpString.split(",");
                List<String> tempArr1 = new ArrayList<>();
                for(int i = 0; i < tempArr.length; i++){
                    String tempString;
                    if(tempArr[i].contains("\"")){
                        tempString = tempArr[i].replace("\"", "");
                        tempString = tempString.concat(",");
                        String quoteString = tempArr[i+1].substring(0, tempArr[i+1].length() - 1);
                        quoteString = quoteString.replace("\"\"", "\"");
                        tempString = tempString.concat(quoteString);
                        tempArr1.add(tempString);
                        i++;
                        continue;
                    }else if(tempArr[i].contains("")){
                        if(tempArr[i].length() < 1) {
                            tempString = tempArr[i].replace("", "null");
                            tempArr1.add(tempString);
                            continue;
                        }
                    }
                    tempArr1.add(tempArr[i]);
                }
                for(int i =0; i < tempArr1.size(); i++){
                    columnList.get(i).column.add(tempArr1.get(i));
                }
            }
        }
        //isFirstLineHeader가 false면, Header가 들어간 headerArr 배열을 첫 라인에 넣지 않고 건너뛴다.
        else{
            for(int i=0; i< headerArr.length; i++){
                ColumnImpl columnImpl = new ColumnImpl();
                columnList.add(columnImpl);
            }
            while(scanner.hasNext()){
                String tpString = scanner.nextLine();
                if(tpString.endsWith(","))
                    tpString = tpString.concat("null");
                String []tempArr = tpString.split(",");
                List<String> tempArr1 = new ArrayList<>();

                for(int i = 0; i < tempArr.length; i++){
                    String tempString;
                    if(tempArr[i].contains("\"")){
                        tempString = tempArr[i].replace("\"", "");
                        tempString = tempString.concat(",");
                        String quoteString = tempArr[i+1].substring(0, tempArr[i+1].length() - 1);
                        quoteString = quoteString.replace("\"\"", "\"");
                        tempString = tempString.concat(quoteString);
                        tempArr1.add(tempString);
                        i++;
                        continue;
                    }else if(tempArr[i].contains("")){
                        if(tempArr[i].length() < 1) {
                            tempString = tempArr[i].replace("", "null");
                            tempArr1.add(tempString);
                            continue;
                        }
                    }
                    tempArr1.add(tempArr[i]);
                }
                for(int i =0; i < tempArr1.size(); i++){
                    columnList.get(i).column.add(tempArr1.get(i));
                }
            }
        }
//        출력테스트용
//        for(int i = 0; i < columnList.get(11).column.size(); i++){
//            System.out.println(columnList.get(11).column.get(i));
//        }
        table.tableList = columnList;

        return table;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table sort(Table table, int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        TableImpl sortTable = new TableImpl();
        TableImpl tableImpl = (TableImpl)table;

        for(int i = 0; i < table.getColumnCount(); i++){
            ColumnImpl sortColumn = new ColumnImpl();
            sortColumn.header = tableImpl.tableList.get(i).header;
            sortTable.tableList.add(sortColumn);
            for(int j = 0; j < table.getRowCount(); j++){
                sortTable.tableList.get(i).column.add(tableImpl.tableList.get(i).column.get(j));
            }
        }
        sortTable.sort(byIndexOfColumn, isAscending, isNullFirst);

        return sortTable;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        TableImpl sortTable = new TableImpl();
        TableImpl tableImpl = (TableImpl)table;

        for(int i = 0; i < table.getColumnCount(); i++){
            ColumnImpl sortColumn = new ColumnImpl();
            sortColumn.header = tableImpl.tableList.get(i).header;
            sortTable.tableList.add(sortColumn);
            for(int j = 0; j < table.getRowCount(); j++){
                sortTable.tableList.get(i).column.add(tableImpl.tableList.get(i).column.get(j));
            }
        }
        sortTable.shuffle();

        return sortTable;
    }
}
