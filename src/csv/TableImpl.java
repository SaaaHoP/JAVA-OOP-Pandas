package csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

class TableImpl implements Table{

    List<ColumnImpl> tableList;
    TableImpl(){
        tableList = new ArrayList<ColumnImpl>();
    }

    @Override
    public String toString(){
        int maxColumnLength = "Column".length() + 5;
        if(!this.tableList.get(0).header.equals("")){
            for(int i = 0; i < this.tableList.size(); i++){
                if(maxColumnLength < this.tableList.get(i).header.length())
                    maxColumnLength = this.tableList.get(i).header.length();
            }
        }

        StringBuffer stringBuffer = new StringBuffer("");
        stringBuffer.append("<" + this.getClass().toString().substring(6).substring(0,9) + "@" + Integer.toHexString(this.hashCode()) + ">\n");
        stringBuffer.append("RangeIndex: " + this.tableList.get(0).count() + " entries, 0 to " + (this.tableList.get(0).count() - 1) + "\n");
        stringBuffer.append("Data columns (total " + this.tableList.size() + " columns):\n");
        stringBuffer.append(" # |     Column |Non-Null Count |Dtype\n");

        for(int i = 0; i < this.tableList.size(); i++){
            if(!this.tableList.get(0).header.equals("")){
                if(i > 9){
                    stringBuffer.append(i + " |" +  String.format("%" + maxColumnLength + "s", this.tableList.get(i).header) + " |  " +
                            (this.tableList.get(i).count() - this.tableList.get(i).getNullCount()) + " non-null |" + this.tableList.get(i).isDtype() + "\n");
                } else {
                    stringBuffer.append(" " + i + " |" +  String.format("%" + maxColumnLength + "s", this.tableList.get(i).header) + " |  " +
                            (this.tableList.get(i).count() - this.tableList.get(i).getNullCount()) + " non-null |" + this.tableList.get(i).isDtype() + "\n");
                }
            }
            else {
                if(i > 9){
                    stringBuffer.append(i + " |" +  String.format("%" + maxColumnLength + "s", "") + " |  " +
                            (this.tableList.get(i).count() - this.tableList.get(i).getNullCount()) + " non-null |" + this.tableList.get(i).isDtype() + "\n");
                } else {
                    stringBuffer.append(" " + i + " |" +  String.format("%" + maxColumnLength + "s", "") + " |  " +
                            (this.tableList.get(i).count() - this.tableList.get(i).getNullCount()) + " non-null |" + this.tableList.get(i).isDtype() + "\n");
                }
            }
        }
        stringBuffer.append(this.getDtypeCount());

        return stringBuffer.toString();
    }

    public void print(){
        int maxColumnLength;

        if(this.tableList.get(0).header.equals("")) {
            for(int i = 0; i <this.tableList.get(0).count(); i++){
                for(int j = 0; j < this.tableList.size(); j++){
                    maxColumnLength = this.tableList.get(j).blankCount();
                    System.out.print(String.format("%" + maxColumnLength + "s", this.tableList.get(j).column.get(i)) + " | ");
                }
                System.out.println();
            }
        }
        else {
            for(int i = 0; i < this.tableList.size(); i++) {
                maxColumnLength = this.tableList.get(i).blankCount();
                System.out.print(String.format("%" + maxColumnLength + "s", this.tableList.get(i).header) + " | ");
            }
            System.out.println();
            for(int i = 0; i <this.tableList.get(0).count(); i++){
                for(int j = 0; j < this.tableList.size(); j++){
                    maxColumnLength = this.tableList.get(j).blankCount();
                    System.out.print(String.format("%" + maxColumnLength + "s", this.tableList.get(j).column.get(i)) + " | ");
                }
                System.out.println();
            }
        }
    }

    //데이터타입 갯수 카운트용 함수 추가
    String getDtypeCount(){
        String count;
        int doubleCount = 0;
        int intCount = 0;
        int stringCount = 0;

        for(int i = 0; i < this.tableList.size(); i++){
            if(this.tableList.get(i).isDtype().equals("double"))
                doubleCount++;
            else if(this.tableList.get(i).isDtype().equals("int"))
                intCount++;
            else if(this.tableList.get(i).isDtype().equals("String"))
                stringCount++;
        }
        count = "dtypes: double(" + doubleCount + "), int(" + intCount + "), String(" + stringCount + ")";

        return count;
    }

    /**
     * String 타입 컬럼이더라도,
     * 그 컬럼에 double로 처리할 수 있는 값이 있다면,
     * 그 값을 대상으로 해당 컬럼 통계량을 산출
     */
    //헤더 없을때는 어떻게 처리하지? -> 헤더가 빈 스트링이면 한줄 빼기
    public Table getStats(){
        TableImpl statsTable = new TableImpl();
        ColumnImpl columnImpl = new ColumnImpl();
        statsTable.tableList.add(columnImpl);

        if(!(this.tableList.get(0).header.equals("")))
            columnImpl.column.add("     ");
        columnImpl.column.add("count");
        columnImpl.column.add(" mean");
        columnImpl.column.add("  std");
        columnImpl.column.add("  min");
        columnImpl.column.add("  25%");
        columnImpl.column.add("  50%");
        columnImpl.column.add("  75%");
        columnImpl.column.add("  max");

        for(int i = 0; i < this.tableList.size(); i++){
            if(this.tableList.get(i).getNumericCount() == 0)
                continue;
            else{
                ColumnImpl columnImpl2 = new ColumnImpl();
                statsTable.tableList.add(columnImpl2);
                if(!(this.tableList.get(0).header.equals("")))
                    columnImpl2.column.add(this.tableList.get(i).header);

            try{
                columnImpl2.column.add(Long.toString(this.tableList.get(i).getNumericCount()));
                if(Double.toString(this.tableList.get(i).getMean()).equals("1.0E-6") || Double.toString(this.tableList.get(i).getMean()).equals("-1.0E-6") )
                    columnImpl2.column.add("0.0");
                else
                    columnImpl2.column.add(Double.toString(this.tableList.get(i).getMean()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getStd()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getNumericMin()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getQ1()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getMedian()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getQ3()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getNumericMax()));
            } catch (NumberFormatException e){
                if(Double.toString(this.tableList.get(i).getMeanT()).equals("1.0E-6") || Double.toString(this.tableList.get(i).getMeanT()).equals("-1.0E-6") )
                    columnImpl2.column.add("0.0");
                else
                    columnImpl2.column.add(Double.toString(this.tableList.get(i).getMeanT()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getStdT()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getNumericMinT()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getQ1T()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getMedianT()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getQ3T()));
                columnImpl2.column.add(Double.toString(this.tableList.get(i).getNumericMaxT()));
                }
            }
        }

        return statsTable;
    }

    /**
     * @return 처음 (최대)5개 행으로 구성된 새로운 Table 생성 후 반환
     */
    public Table head(){
        TableImpl headTable = new TableImpl();

        if(!(this.tableList.get(0).header.equals(""))){
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl(this.tableList.get(i).header);
                headTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = 0; j < 5; j++){
                    headTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }
        else{
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl();
                headTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = 0; j < 5; j++){
                    headTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }

        return headTable;
    }
    /**
     * @return 처음 (최대)lineCount개 행으로 구성된 새로운 Table 생성 후 반환
     */
    public Table head(int lineCount){
        TableImpl headTable = new TableImpl();

        if(!(this.tableList.get(0).header.equals(""))){
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl(this.tableList.get(i).header);
                headTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = 0; j < lineCount; j++){
                    headTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }
        else{
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl();
                headTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = 0; j < lineCount; j++){
                    headTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }

        return headTable;
    }

    /**
     * @return 마지막 (최대)5개 행으로 구성된 새로운 Table 생성 후 반환
     */
    public Table tail(){
        TableImpl tailTable = new TableImpl();
        int tailCount = this.tableList.get(0).column.size();

        if(!(this.tableList.get(0).header.equals(""))){
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl(this.tableList.get(i).header);
                tailTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = tailCount - 5; j < tailCount; j++){
                    tailTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }
        else{
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl();
                tailTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = tailCount - 5; j < tailCount; j++){
                    tailTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }

        return tailTable;
    }
    /**
     * @return 마지막 (최대)lineCount개 행으로 구성된 새로운 Table 생성 후 반환
     */
    public Table tail(int lineCount){
        TableImpl tailTable = new TableImpl();
        int tailCount = this.tableList.get(0).column.size();

        if(!(this.tableList.get(0).header.equals(""))){
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl(this.tableList.get(i).header);
                tailTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = tailCount - lineCount; j < tailCount; j++){
                    tailTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }
        else{
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl();
                tailTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = tailCount - lineCount; j < tailCount; j++){
                    tailTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }

        return tailTable;
    }

    /**
     * @param beginIndex 포함(이상)
     * @param endIndex 미포함(미만)
     * @return 검색 범위에 해당하는 행으로 구성된 새로운 Table 생성 후 반환
     */
    public Table selectRows(int beginIndex, int endIndex){
        TableImpl selectTable = new TableImpl();

        if(!(this.tableList.get(0).header.equals(""))){
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl(this.tableList.get(i).header);
                selectTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = beginIndex; j < endIndex; j++){
                    selectTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }
        else{
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl();
                selectTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = beginIndex; j < endIndex; j++){
                    selectTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }

        return selectTable;
    }

    /**
     * @return 검색 인덱스에 해당하는 행으로 구성된 새로운 Table 생성 후 반환
     */
    public Table selectRowsAt(int ...indices){
        TableImpl selectTable = new TableImpl();

        if(!(this.tableList.get(0).header.equals(""))) {
            for (int i = 0; i < this.tableList.size(); i++) {
                ColumnImpl columnImpl = new ColumnImpl(this.tableList.get(i).header);
                selectTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = 0; j < indices.length; j++){
                    selectTable.tableList.get(i).column.add(this.tableList.get(i).column.get(indices[j]));
                }
            }
        }
        else {
            for(int i = 0; i < this.tableList.size(); i++){
                ColumnImpl columnImpl = new ColumnImpl();
                selectTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < this.tableList.size(); i++){
                for(int j = 0; j < indices.length; j++){
                    selectTable.tableList.get(i).column.add(this.tableList.get(i).column.get(indices[j]));
                }
            }
        }

        return selectTable;
    }

    /**
     * @param beginIndex 포함(이상)
     * @param endIndex 미포함(미만)
     * @return 검색 범위에 해당하는 열로 구성된 새로운 Table 생성 후 반환
     */
    public Table selectColumns(int beginIndex, int endIndex){
        TableImpl selectTable = new TableImpl();

        if(!(this.tableList.get(0).header.equals(""))) {
            for (int i = beginIndex; i < endIndex; i++) {
                ColumnImpl columnImpl = new ColumnImpl(this.tableList.get(i).header);
                selectTable.tableList.add(columnImpl);
            }
            for(int i = beginIndex; i < endIndex; i++){
                for(int j = 0; j < this.tableList.get(0).column.size(); j++){
                    selectTable.tableList.get(i - beginIndex).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }
        else {
            for(int i = beginIndex; i < endIndex; i++){
                ColumnImpl columnImpl = new ColumnImpl();
                selectTable.tableList.add(columnImpl);
            }
            for(int i = beginIndex; i < endIndex; i++){
                for(int j = 0; j < this.tableList.get(0).column.size(); j++){
                    selectTable.tableList.get(i).column.add(this.tableList.get(i).column.get(j));
                }
            }
        }

        return selectTable;
    }

    /**
     * @return 검색 인덱스에 해당하는 열로 구성된 새로운 Table 생성 후 반환
     */
    public Table selectColumnsAt(int ...indices){
        TableImpl selectTable = new TableImpl();

        if(!(this.tableList.get(0).header.equals(""))) {
            for (int i = 0; i < indices.length; i++) {
                ColumnImpl columnImpl = new ColumnImpl(this.tableList.get(indices[i]).header);
                selectTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < indices.length; i++){
                for(int j = 0; j < this.tableList.get(0).column.size(); j++){
                    selectTable.tableList.get(i).column.add(this.tableList.get(indices[i]).column.get(j));
                }
            }
        }
        else {
            for(int i = 0; i < indices.length; i++){
                ColumnImpl columnImpl = new ColumnImpl();
                selectTable.tableList.add(columnImpl);
            }
            for(int i = 0; i < indices.length; i++){
                for(int j = 0; j < this.tableList.get(0).column.size(); j++){
                    selectTable.tableList.get(i).column.add(this.tableList.get(indices[i]).column.get(j));
                }
            }
        }

        return selectTable;
    }

    /**
     * @param
     * @return 검색 조건에 해당하는 행으로 구성된 새로운 Table 생성 후 반환, 제일 나중에 구현 시도하세요.
     */
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate){
        TableImpl selectTable = new TableImpl();
        List <List <String>> tempTable = new ArrayList<>();
        List <List <String>> rowsTable = new ArrayList<>();

        for(int i = 0; i < this.getRowCount(); i++){
            List <String> tempRow = new ArrayList<>();
            tempTable.add(tempRow);
            for(int j = 0; j < this.getColumnCount(); j++){
                tempTable.get(i).add(this.tableList.get(j).column.get(i));
            }
        }

        for(int i = 0; i < this.getColumnCount(); i++){
            if(this.tableList.get(i).header.equals(columnName)) {
                    for (int j = 0; j < this.getRowCount(); j++) {
                            try {
                                if(this.tableList.get(i).column.get(j).equals("null") && predicate.test(null)){
                                    rowsTable.add(tempTable.get(j));
                                    continue;
                                }
                                if (predicate.test((T) this.tableList.get(i).column.get(j)))
                                    rowsTable.add(tempTable.get(j));
                            } catch (ClassCastException | NumberFormatException | NullPointerException e) {
                                try{
                                    if(predicate.test((T)this.tableList.get(i).getValue(j,Integer.class)))
                                        rowsTable.add(tempTable.get(j));
                                } catch (ClassCastException | NumberFormatException | NullPointerException c) {
                                    try{
                                        if(predicate.test((T)this.tableList.get(i).getValue(j,Double.class)))
                                            rowsTable.add(tempTable.get(j));
                                    } catch (ClassCastException | NumberFormatException | NullPointerException n) {
                                        continue;
                                    }
                                }
                            }
                    }
                }
            }

        for(int i = 0; i < this.getColumnCount(); i++){
            ColumnImpl sortColumn = new ColumnImpl();
            sortColumn.header = this.tableList.get(i).header;
            selectTable.tableList.add(sortColumn);
            for(int j = 0; j < rowsTable.size(); j++){
                selectTable.tableList.get(i).column.add(rowsTable.get(j).get(i));
            }
        }

        return selectTable;
    }

    /**
     * @return 원본 Table이 정렬되어 반환된다.
     */
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst){
        List <List <String>> tempTable = new ArrayList<>();
        List <List <String>> sortTable = new ArrayList<>();
        List <List <String>> nullTable = new ArrayList<>();

        for(int i = 0; i < this.getRowCount(); i++){
            List <String> tempRow = new ArrayList<>();
            tempTable.add(tempRow);
            for(int j = 0; j < this.getColumnCount(); j++){
                tempTable.get(i).add(this.tableList.get(j).column.get(i));
            }
        }

        for(int i = 0; i < this.tableList.get(byIndexOfColumn).column.size(); i++){
            if(this.tableList.get(byIndexOfColumn).column.get(i).equals("null")){
                nullTable.add(tempTable.get(i));
            }
            else{
                sortTable.add(tempTable.get(i));
            }
        }

        if(isAscending){
            try{
                for (int i = 0; i < sortTable.size(); i++) {
                for (int j = i + 1; j < sortTable.size(); j++) {
                    if(Double.parseDouble(sortTable.get(i).get(byIndexOfColumn)) > Double.parseDouble(sortTable.get(j).get(byIndexOfColumn))){
                        List <String> tempString = sortTable.get(i);
                        sortTable.set(i, sortTable.get(j));
                        sortTable.set(j, tempString);
                    }
                }
            }
            } catch (NumberFormatException e){
                for (int i = 0; i < sortTable.size(); i++) {
                    for (int j = i + 1; j < sortTable.size(); j++) {
                        if(sortTable.get(i).get(byIndexOfColumn).compareTo(sortTable.get(j).get(byIndexOfColumn)) > 0){
                            List <String> tempString = sortTable.get(i);
                            sortTable.set(i, sortTable.get(j));
                            sortTable.set(j, tempString);
                        }
                    }
                }
            }
        }
        else {
            try {
                for (int i = 0; i < sortTable.size(); i++) {
                    for (int j = i + 1; j < sortTable.size(); j++) {
                        if(Double.parseDouble(sortTable.get(i).get(byIndexOfColumn)) < Double.parseDouble(sortTable.get(j).get(byIndexOfColumn))){
                            List <String> tempString = sortTable.get(i);
                            sortTable.set(i, sortTable.get(j));
                            sortTable.set(j, tempString);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                for (int i = 0; i < sortTable.size(); i++) {
                    for (int j = i + 1; j < sortTable.size(); j++) {
                        if(sortTable.get(i).get(byIndexOfColumn).compareTo(sortTable.get(j).get(byIndexOfColumn)) < 0){
                            List <String> tempString = sortTable.get(i);
                            sortTable.set(i, sortTable.get(j));
                            sortTable.set(j, tempString);
                        }
                    }
                }
            }
        }

        if(isNullFirst){
            sortTable.addAll(0, nullTable);
        } else {
            sortTable.addAll(nullTable);
        }

        for(int i = 0; i < this.getColumnCount(); i++){
            for(int j = 0; j < this.getRowCount(); j++){
                this.tableList.get(i).setValue(j, sortTable.get(j).get(i));
            }
        }

        return this;
    }

    /**
     * @return 원본 Table이 무작위로 뒤섞인 후 반환된다. 말 그대로 랜덤이어야 한다. 즉, 랜덤 로직이 존재해야 한다.
     */
    public Table shuffle(){
        List <List <String>> tempTable = new ArrayList<>();

        for(int i = 0; i < this.getRowCount(); i++){
            List <String> tempRow = new ArrayList<>();
            tempTable.add(tempRow);
            for(int j = 0; j < this.getColumnCount(); j++){
                tempTable.get(i).add(this.tableList.get(j).column.get(i));
            }
        }
        Collections.shuffle(tempTable);

        for(int i = 0; i < this.getColumnCount(); i++){
            for(int j = 0; j < this.getRowCount(); j++){
                this.tableList.get(i).setValue(j, tempTable.get(j).get(i));
            }
        }

        return this;
    }

    public int getRowCount(){
        return this.tableList.get(0).count();
    }
    public int getColumnCount(){
        return this.tableList.size();
    }


    /**
     * @return 원본 Column이 반환된다. 따라서, 반환된 Column에 대한 조작은 원본 Table에 영향을 끼친다.
     */
    public Column getColumn(int index){
        return this.tableList.get(index);
    }
    /**
     * @return 원본 Column이 반환된다. 따라서, 반환된 Column에 대한 조작은 원본 Table에 영향을 끼친다.
     */
    public Column getColumn(String name){
        for(int i = 0; i < this.tableList.size(); i++){
            if(this.tableList.get(i).header.equals(name))
                return this.tableList.get(i);
            else
                continue;
        }

        return null;
    }

    /**
     * String 타입 컬럼들에는 영향을 끼치지 않는다.
     * double 혹은 int 타입 컬럼들에 한해서 null 값을 mean 값으로 치환한다.
     * 이 연산 후, int 타입 컬럼에 mean으로 치환된 cell이 있을 경우, 이 컬럼은 double 타입 컬럼으로 바뀐다.
     * 왜냐하면, mean 값이 double이기 때문이다.
     * @return 테이블에 mean으로 치환한 cell이 1개라도 발생했다면, true 반환
     */
    public boolean fillNullWithMean(){
        boolean isFilledMean = false;

        for(int i = 0; i < this.tableList.size(); i++) {
            try {
                if (this.tableList.get(i).fillNullWithMean())
                    isFilledMean = true;
                else
                    continue;
            } catch (NumberFormatException e) {
                    continue;
            }
        }

        return isFilledMean;
    }

    /**
     * String 타입 컬럼들에는 영향을 끼치지 않는다.
     * double 혹은 int 타입 컬럼들에 한해서 null 값을 0으로 치환한다.
     * 이 연산 후, int 타입 혹은 double 타입 컬럼 모두 그 타입이 유지된다.
     * @return 테이블에 0으로 치환한 cell이 1개라도 발생했다면, true 반환
     */
    public boolean fillNullWithZero(){
        boolean isFilledZero = false;

        for(int i = 0; i < this.tableList.size(); i++) {
            try {
                if (this.tableList.get(i).fillNullWithZero())
                    isFilledZero = true;
                else
                    continue;
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return isFilledZero;
    }

    /**
     * 평균 0, 표준편자 1인 컬럼으로 바꾼다. (null은 연산 후에도 null로 유지된다. 즉, null은 연산 제외)
     * String 타입 컬럼들에는 영향을 끼치지 않는다.
     * double 혹은 int 타입 컬럼들에 한해서 수행된다.
     * 이 연산 후, int 타입 컬럼은 double 타입 컬럼으로 바뀐다.
     * 왜냐하면, mean과 std가 double이기 때문이다.
     * @return 이 연산에 의해 값이 바뀐 열이 1개라도 발생했다면, true 반환
     */
    public boolean standardize(){
        boolean isStandardize = false;

        for(int i = 0; i < this.tableList.size(); i++) {
            try {
                if (this.tableList.get(i).standardize())
                    isStandardize = true;
                else
                    continue;
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return isStandardize;
    }

    /**
     * 최솟값 0, 최댓값 1인 컬럼으로 바꾼다. (null은 연산 후에도 null로 유지된다.즉, null은 연산 제외)
     * String 타입 컬럼들에는 영향을 끼치지 않는다.
     * double 혹은 int 타입 컬럼들에 한해서 수행된다.
     * 이 연산 후, int 타입 컬럼은 double 타입 컬럼으로 바뀐다.
     * 왜냐하면, 0과 1사이의 값들은 double이기 때문이다.
     * @return 이 연산에 의해 값이 바뀐 열이 1개라도 발생했다면, true 반환
     */
    public boolean normalize(){
        boolean isNormalize = false;

        for(int i = 0; i < this.tableList.size(); i++) {
            try {
                if (this.tableList.get(i).normalize())
                    isNormalize = true;
                else
                    continue;
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return isNormalize;
    }


    /**
     * null을 제외하고 2가지 값으로만 구성된 컬럼이기만 하면 수행된다.
     * 연산 후 0과 1로 구성된 컬럼으로 바뀐다. (null은 연산 후에도 null로 유지된다.즉, null은 연산 제외)
     * 모든 타입 컬럼들에 대해서 수행될 수 있다.
     * 이 연산이 수행된 컬럼은 int 타입 컬럼으로 바뀐다.
     * 왜냐하면, 0과 1이 int이기 때문이다.
     * @return 이 연산에 의해 값이 바뀐 열이 1개라도 발생했다면, true 반환
     */
    public boolean factorize() {
        boolean isFactorize = false;

        for(int i = 0; i < this.tableList.size(); i++) {
            try {
                if (this.tableList.get(i).factorize())
                    isFactorize = true;
                else
                    continue;
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return isFactorize;
    }
}
