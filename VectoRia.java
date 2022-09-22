//LSEdit: 22.9.22
import java.util.ArrayList;
class VectoRia
{
    //Main Method
    public static void main(String[] args)
    {
        String toEncode="jack=+";//String to encode
        String key="lbvm/s";//String to key
        int decoy=7;
        System.out.println(toEncode+" Encoding...........");
        try
        {
            String encoded = new VectoRia().Encode(toEncode, key, decoy);//encoded text
            System.out.println("Encoded.");
            System.out.printf("Encoded message:\t%s%n",encoded);//Print encoded
            System.out.println("Decoding........");
            String decoded=new VectoRia().decode(encoded,key,decoy);//Decode the encoded text
            System.out.println("Decoded.");
            System.out.printf("Decoded message:\t%s%n",decoded);//Print decoded
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());//errors
        }
    }
    //Encode
    public String Encode(String original,String key, int decoy) throws KeyLengthException
    {
        decoy=Math.abs(decoy);//decoy cannot be negative
        //shift_decoy is the amount of shift that has to be done to create the encoded text
        int shift_decoy=(decoy>65536)?decoy-(65536*(decoy/65536)):decoy;//shift_decoy cannot be greater than 65536
        if(key.length()!=original.length())//key length should be equal to original length
        {
            throw new KeyLengthException();
        }
        int calc[][]=new int[2][key.length()];//table to store the characters of original and key
        for(int i=0;i<2;i++)
        {
            for(int j=0;j<key.length();j++)
            {
                if(i==0)
                {
                    //first row to store the number representation of characters of original
                    calc[i][j] = original.charAt(j);
                }
                else
                {
                    //second row to store the number representation of characters of key
                    calc[i][j] = key.charAt(j);
                }
            }
        }
        ArrayList<Integer> answer_int=new ArrayList<>();//store the integers obtained to represent each character of the encoded text
        ArrayList<ArrayList<Integer>> done=new ArrayList<>();//store the cells of tables that have already been done
        //the encoding process
        for(int i=0;i<key.length();i++)
        {
            for(int j=0;j<key.length()-1;j++)
            {
                if (j!=i && j+1!=i)
                {
                    int first=calc[0][j]+calc[1][j+1]+shift_decoy,second=calc[1][j]+calc[0][j+1]+shift_decoy;
                    ArrayList<Integer> check=new ArrayList<>();
                    check.add(j);
                    check.add(j+1);
                    if (first > 65536)
                        first = first - 65536;

                    if (second > 65536)
                        second = second - 65536;
                    if(!done.contains(check))
                    {
                        answer_int.add(first);
                        answer_int.add(second);
                    }
                    done.add(check);
                }
            }
        }
        String answer_final="";//represent each integer of answer_int by characters
        for(int i=0;i<answer_int.size();i++)
        {
            answer_final+=(char)((int)answer_int.get(i));
        }
        return decoy(answer_final,decoy);//return final answer after putting a decoy
    }
    //Decode
    public String decode(String encoded,String key,int decoy)
    {
        decoy=Math.abs(decoy);//decoy cannot be negative
        //shift_decoy is the amount of shift that has been done to create the encoded text
        int shift_decoy=(decoy>65536)?decoy-(65536*(decoy/65536)):decoy;//shift_decoy cannot be greater than 65536
        encoded=anti_decoy(encoded,decoy);//remove the decoyed letters
        int calc[][]=new int[2][key.length()];//table for putting the integer representation of characters of encoded
        for(int i=0;i<2;i++)
        {
            for(int j=0;j<key.length();j++)
            {
                if(i==0)
                    calc[i][j]=-500;//when row is 1, leave the cell with -500, which will later be used for calculating the original text
                else
                {
                    //second row to store the number representation of characters of key
                    calc[i][j] = key.charAt(j);
                }
            }
        }
        ArrayList<Integer> answer_int=new ArrayList<>();//integer representation of characters of the encoded text
        for(int i=0;i<encoded.length();i++)
        {
            //store integer representation of characters of encoded text to the ArrayList
            answer_int.add((int)encoded.charAt(i));
        }
        //decoding process
        int counter=0;
        for(int i=0;i<key.length();i++)
        {
            for(int j=0;j<key.length()-1;j++)
            {
                if (j!=i && j+1!=i)
                {
                    if(calc[0][j]==-500 || calc[0][j+1]==-500)
                    {
                        calc[0][j] = answer_int.get(counter++) - calc[1][j + 1]-shift_decoy;
                        calc[0][j + 1] = answer_int.get(counter++) - calc[1][j]-shift_decoy;
                    }
                    if(calc[0][j]<0)
                        calc[0][j]=calc[0][j]+65536;
                    if(calc[0][j+1]<0)
                        calc[0][j+1]=calc[0][j+1]+65536;
                }
            }
        }
        String answer_final="";//to store the answer
        for(int i=0;i<key.length();i++)
        {
            //convert the integers of the answer from the table to Letters
            answer_final+=(char)(calc[0][i]);
        }
        return answer_final;//return answer
    }
    //decoy
    String decoy(String s,int n)
    {
        //add n number of random letters to create a decoy
        for(int i=0;i<n;i++)
        {
            if(s.contains("+"))
            {
                if(getRandomNumber(0,10)%7==0)
                    s += "+";
                else
                {
                    if(getRandomNumber(0,10)%2==0)
                        s+=(char) getRandomNumber(65, 91);
                    else
                        s+=(char) getRandomNumber(97, 123);
                }
            }
            else
            {
                if(getRandomNumber(0,10)%2==0)
                    s+=(char) getRandomNumber(65, 91);
                else
                    s+=(char) getRandomNumber(97, 123);
            }
        }
        return s;
    }
    //anti_decoy
    String anti_decoy(String s,int n)
    {
        return s.substring(0,s.length()-n);//remove the decoy letters
    }
    //random number generator
    int getRandomNumber(int min, int max)
    {
        return (int) ((Math.random() * (max - min)) + min);
    }
    //exceptions
    static class KeyLengthException extends Exception{
        public KeyLengthException() {
            super();
        }

        @Override
        public String getMessage() {
            return "Length of key cannot be different from the length of String";
        }

    }
}
