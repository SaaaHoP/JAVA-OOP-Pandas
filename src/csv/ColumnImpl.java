package csv;

import java.util.ArrayList;
import java.util.List;

class ColumnImpl implements Column{
    List<String> column;
    String header = "";

    ColumnImpl(){
        this.column = new ArrayList<>();
    }
    ColumnImpl(String header){
        this.column = new ArrayList<>();
        this.header = header;
    }

    //데이터 타입 체크용 추가 함수
    String isDtype () {
        String isDtype = "";
        List <String> tempList = new ArrayList<>();

        for (int i = 0; i < this.column.size(); i++) {
            if(this.column.get(i).equals("null"))
                continue;
            try {
                tempList.add(this.column.get(i));
                Double.parseDouble(this.column.get(i));
            } catch (NumberFormatException e){
                return "String";
            }
        }
        for(int i = 0; i < tempList.size(); i++){
            if(tempList.get(i).equals("null"))
                continue;
            try {
                Integer.parseInt(tempList.get(i));
                isDtype = "int";
            }catch (NumberFormatException e){
                return "double";
            }
        }

        return isDtype;
    }

    //문자열 print할때 얼만큼 공백 띄워줘야하는지 알려주는 함수
    int blankCount() {
        int maxColumnLength = this.header.length();

        for(int i = 0; i < this.column.size(); i++){
            if(maxColumnLength < this.column.get(i).length())
                maxColumnLength = this.column.get(i).length();
        }

        return maxColumnLength;
    }

    public String getHeader(){
        if(this.header.equals(""))
            return null;
        return this.header;
    }

    /* cell 값을 String으로 반환 */
    public String getValue(int index){
        return this.column.get(index);
    }

    /**
     * @param index
     * @param t 가능한 값으로 Double.class, Integer.class
     * @return Double 혹은 Integer로 반환 불가능할 시, 예외 발생
     */
    public <T extends Number> T getValue(int index, Class<T> t){
        if(t == Double.class)
            return (T) Double.valueOf(this.column.get(index));
        else if(t == Integer.class)
            return (T) Integer.valueOf(this.column.get(index));
        else
            throw new IllegalArgumentException("Unknown type");
    }

    public void setValue(int index, String value){
        this.column.set(index, value);
    }

    /**
     * @param value double, int 리터럴을 index의 cell로 건네고 싶을 때 사용
     */
    public <T extends Number> void setValue(int index, T value){
        this.setValue(index, value.toString());
    }

    /**
     * @return null 포함 모든 cell 개수 반환
     */
    public int count(){
        return this.column.size();
    }

    public void print(){
        for(int i = 0; i < this.column.size(); i++){
            System.out.print(this.column.get(i));
            System.out.println();
        }
    }

    /**
     * @return (int or null)로 구성된 컬럼 or (double or null)로 구성된 컬럼이면 true 반환
     */
    public boolean isNumericColumn(){
        for(int i = 0; i< this.column.size(); i++){
            if(this.column.get(i).equals("null")){
                continue;
            }
                try {
                    Integer.parseInt(this.column.get(i));
                } catch(NumberFormatException e) {
                    try{
                        Double.parseDouble(this.column.get(i));
                    } catch (NumberFormatException e1){
                        return false;
                    }
                }
        }

        return true;
    }

    public long getNullCount(){
        long nullCount = 0;

        for(int i = 0; i < this.column.size(); i++){
            if(this.column.get(i).equals("null"))
                nullCount++;
        }

        return nullCount;
    }

    /**
     * @return int 혹은 double로 평가될 수 있는 cell의 개수
     */
    public long getNumericCount(){
        long cellCount = 0;

        for(int i = 0; i< this.column.size(); i++){
            if(this.column.get(i).equals("null")){
                continue;
            }
            try {
                Integer.parseInt(this.column.get(i));
                cellCount++;
            } catch(NumberFormatException e) {
                try{
                    Double.parseDouble(this.column.get(i));
                    cellCount++;
                } catch (NumberFormatException e1){
                    continue;
                }
            }
        }

        return cellCount;
    }

    // 아래 7개 메소드는 String 타입 컬럼에 대해서 수행 시, 예외 발생 시켜라.
    public double getNumericMin(){
        double min = this.getValue(0, Double.class);

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    if(min > this.getValue(i, Double.class))
                        min = this.getValue(i, Double.class);
                    else
                        continue;
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String Type Column");
            }
        }

        return min;
    }

    public double getNumericMax(){
        double max = 0;

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    if(max < this.getValue(i, Double.class))
                        max = this.getValue(i, Double.class);
                    else
                        continue;
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String Type Column");
            }
        }

        return max;
    }

    public double getMean(){
        double mean;
        double temp = 0;
        double count = 0;
        double result;

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    temp += Double.parseDouble(this.column.get(i));
                    count++;
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String Type Column");
            }
        }
        mean = temp / count;
        result = Math.round((mean * 1000000)) / 1000000.0;

        return result;
    }

    public double getStd(){
        double std;
        double temp = 0;
        double count = 0;
        double mean = this.getMean();
        double result;

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    temp += Math.pow(Double.parseDouble(this.column.get(i)) - mean, 2);
                    count++;
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String Type Column");
            }
        }
        std = Math.sqrt(temp / count);
        result = Math.round((std * 1000000)) / 1000000.0;

        return result;
    }

    public double getQ1(){
        double q1;
        double result;
        List <Double> arr = new ArrayList<>();

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    arr.add(Double.parseDouble(this.column.get(i)));
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String Type Column");
            }

        }
        arr.sort(null);
        double index = 0.25 * (arr.size()-1);
        int lower = (int)Math.floor(index);
        double fraction = index - lower;
        q1 = arr.get(lower) + fraction * (arr.get(lower+1) - arr.get(lower));
        result = Math.round((q1 * 1000000)) / 1000000.0;

        return result;
    }

    public double getMedian(){
        double median;
        double result;
        List <Double> arr = new ArrayList<>();

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    arr.add(Double.parseDouble(this.column.get(i)));
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String Type Column");
            }
        }
        arr.sort(null);
        double index = 0.50 * (arr.size()-1);
        int lower = (int)Math.floor(index);
        double fraction = index - lower;
        median = arr.get(lower) + fraction * (arr.get(lower+1) - arr.get(lower));
        result = Math.round((median * 1000000)) / 1000000.0;

        return result;
    }

    public double getQ3(){
        double q3;
        double result;
        List <Double> arr = new ArrayList<>();

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    arr.add(Double.parseDouble(this.column.get(i)));
                }
            } catch (NumberFormatException e) {
                throw new NumberFormatException("String Type Column");
            }
        }
        arr.sort(null);
        double index = 0.75 * (arr.size()-1);
        int lower = (int)Math.floor(index);
        double fraction = index - lower;
        q3 = arr.get(lower) + fraction * (arr.get(lower+1) - arr.get(lower));
        result = Math.round((q3 * 1000000)) / 1000000.0;

        return result;
    }

    //컬럼안에 스트링, 숫자형 섞여있는 전용 통계 추가 함수들
    double getNumericMinT(){
        double min = 0;

        for(int i = 0; i < this.column.size(); i++){
            try{
                min = Double.parseDouble(this.column.get(i));
                break;
            } catch (NumberFormatException e){
                continue;
            }
        }
        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    if(min > Double.parseDouble(this.column.get(i)))
                        min = Double.parseDouble(this.column.get(i));
                    else
                        continue;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return min;
    }

    double getNumericMaxT(){
        double max = 0;

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    if(max < Double.parseDouble(this.column.get(i)))
                        max = Double.parseDouble(this.column.get(i));
                    else
                        continue;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return max;
    }

    double getMeanT(){
        double mean;
        double temp = 0;
        double count = 0;
        double result;

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    temp += Double.parseDouble(this.column.get(i));
                    count++;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        mean = temp / count;
        result = Math.round((mean * 1000000)) / 1000000.0;

        return result;
    }

    double getStdT(){
        double std;
        double temp = 0;
        double count = 0;
        double mean = this.getMeanT();
        double result;

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    temp += Math.pow(Double.parseDouble(this.column.get(i)) - mean, 2);
                    count++;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        std = Math.sqrt(temp / count);
        result = Math.round((std * 1000000)) / 1000000.0;

        return result;
    }

    double getQ1T(){
        double q1;
        double result;
        List <Double> arr = new ArrayList<>();

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    arr.add(Double.parseDouble(this.column.get(i)));
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        arr.sort(null);
        double index = 0.25 * (arr.size()-1);
        int lower = (int)Math.floor(index);
        double fraction = index - lower;
        q1 = arr.get(lower) + fraction * (arr.get(lower+1) - arr.get(lower));
        result = Math.round((q1 * 1000000)) / 1000000.0;

        return result;
    }

    double getMedianT(){
        double median;
        double result;
        List <Double> arr = new ArrayList<>();

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    arr.add(Double.parseDouble(this.column.get(i)));
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        arr.sort(null);
        double index = 0.50 * (arr.size()-1);
        int lower = (int)Math.floor(index);
        double fraction = index - lower;
        median = arr.get(lower) + fraction * (arr.get(lower+1) - arr.get(lower));
        result = Math.round((median * 1000000)) / 1000000.0;

        return result;
    }

    double getQ3T(){
        double q3;
        double result;
        List <Double> arr = new ArrayList<>();

        for(int i = 0; i< this.column.size(); i++) {
            try {
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    arr.add(Double.parseDouble(this.column.get(i)));
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        arr.sort(null);
        double index = 0.75 * (arr.size()-1);
        int lower = (int)Math.floor(index);
        double fraction = index - lower;
        q3 = arr.get(lower) + fraction * (arr.get(lower+1) - arr.get(lower));
        result = Math.round((q3 * 1000000)) / 1000000.0;

        return result;
    }

    // 아래 2개 메소드는 1개 cell이라도 치환했으면, true 반환.
    // String타입 컬럼에 접근하면 false 반환하기
    public boolean fillNullWithMean(){
        boolean isFilled = false;
        double mean = this.getMean();

        try{
            Double.parseDouble(this.column.get(0));
            for(int i = 0; i < this.column.size(); i++){
                if(this.column.get(i).equals("null")){
                    this.column.set(i, Double.toString(mean));
                    isFilled = true;
                }
            }
        }catch (NumberFormatException e){
            return false;
        }

        return isFilled;
    }

    public boolean fillNullWithZero(){
        boolean isFilled = false;

        try{
            Double.parseDouble(this.column.get(0));
            for(int i = 0; i < this.column.size(); i++){
                if(this.column.get(i).equals("null")){
                    this.column.set(i, "0");
                    isFilled = true;
                }
            }
        }catch (NumberFormatException e) {
            return false;
        }

        return isFilled;
    }

    // 아래 3개 메소드는 null 값은 메소드 호출 후에도 여전히 null.
    // standardize()와 normalize()는 String 타입 컬럼에 대해서는 false 반환
    // factorize()는 컬럼 타입과 무관하게 null 제외하고 2가지 값만으로 구성되었다면 수행된다. 조건에 부합하여 수행되었다면 true 반환
    public boolean standardize(){
        boolean isStandardized = false;
        double mean = this.getMean();
        double std = this.getStd();
        double result;

        try {
            Double.parseDouble(this.column.get(0));
            for(int i = 0; i < this.column.size(); i++){
                if(this.column.get(i).equals("null"))
                    continue;
                else{
                    double value = (Double.parseDouble(this.column.get(i)) - mean) / std;
                    result = Math.round((value * 1000000)) / 1000000.0;
                    this.column.set(i, Double.toString(result));
                    isStandardized = true;
                }
            }
        } catch(NumberFormatException e){
            return isStandardized;
        }

        return isStandardized;
    }

    public boolean normalize(){
        boolean isNormalized = false;
        double max = this.getNumericMax();
        double min = this.getNumericMin();
        double result;

        try {
            Double.parseDouble(this.column.get(0));
            for (int i = 0; i < this.column.size(); i++) {
                if (this.column.get(i).equals("null"))
                    continue;
                else {
                    double value = (Double.parseDouble(this.column.get(i)) - min) / (max - min);
                    result = Math.round((value * 1000000)) / 1000000.0;
                    this.column.set(i, Double.toString(result));
                    isNormalized = true;
                }
            }
        } catch(NumberFormatException e) {
            return isNormalized;
        }

        return isNormalized;
    }

    public boolean factorize(){
        boolean isFactorized = false;
        List <String> tempList = new ArrayList<>();

        for(int i = 0; i < this.column.size(); i++){
            if(this.column.get(i).equals("null"))
                continue;
            else{
                tempList.add(this.column.get(i));
                break;
            }
        }

        for(int i = 0; i < this.column.size(); i++) {
            if (tempList.contains(this.column.get(i)))
                continue;
            else if (this.column.get(i).equals("null"))
                continue;
            else
                tempList.add(this.column.get(i));
        }

        if(tempList.size() != 2)
            return false;

        for(int i = 0; i < this.column.size(); i++){
            if(this.column.get(i).equals("null"))
                continue;
            else if(this.column.get(i).equals(tempList.get(0)))
                this.column.set(i, "0");
            else if(this.column.get(i).equals(tempList.get(1)))
                this.column.set(i, "1");
            isFactorized = true;
        }

        return isFactorized;
    }
}
