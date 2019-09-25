/* 
* ScanCode.java 
* Author:Jingyu Feng 
*/

/* The syntax of comments for some common programming languages are addded to "config.properties".
* If you want to add more for specific programming languages, you can add them to "config.properties" like following:
*
*   [extension]_comment=#
*
*   Example(python):
*
*   py_comment=#
*   py_blockComment='''
*   py_endBlockComment='''
* 
*/

import java.io.*;
import java.util.Properties;


public class ScanCode
{

	public static void main(String[] args) throws IOException {

        // get the file name
        String fileName = "";
        if (args.length == 1){
            fileName = args[0];
        } else {
            System.out.println("Usage: java ScanCode.java [fileName]");  
            return;
        }
        
        String todoString = "TODO";
        String line = null;

        try {
            // find the suffix of the file
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex == -1){
                System.out.println("Unable to identify the type of the file '" + fileName + "'"); 
            }
            String suffix = fileName.substring(lastDotIndex + 1);

            // read the file
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // read the comment syntax from "config.properties"
            InputStream input = new FileInputStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            
            String comment = prop.getProperty(suffix + "_comment");
            String blockComment = prop.getProperty(suffix + "_blockComment");
            String endBlockComment = prop.getProperty(suffix + "_endBlockComment");

            int NumOfLines = 0;
            int NumOfSingleCommentLines = 0;
            int NumOfBlockCommentLines = 0;
            int NumOfBlockComments = 0;
            int NumOfCommentLines = 0;
            int NumOfTodos = 0;

            boolean isBlockComment = false;

            // check if blockComment and endBlockComment are the same
            
            while((line = bufferedReader.readLine()) != null) {

                NumOfLines++;

                // find out the index of the comments in current line.
                int singleCommentIdx = line.indexOf(comment);
                int blockCommentIdx = -1;
                int endBlockCommentIdx = -1;

                // find the index of start of block comments only if isBlockComment is false
                if (isBlockComment){
                    endBlockCommentIdx = line.indexOf(endBlockComment);
                } else {
                    blockCommentIdx = line.indexOf(blockComment);
                }

                // check if it contains Todo
                if ((singleCommentIdx != -1 || isBlockComment) && line.contains(todoString)){
                    NumOfTodos++;
                }

                // check if it is a block comment
                if (isBlockComment){
                    NumOfBlockCommentLines++;
                    NumOfCommentLines++;
                    if (endBlockCommentIdx > -1){
                        isBlockComment = false;
                    } else{
                        continue;
                    }
                }

                // check if single comment starts before the block comment
                if (singleCommentIdx != -1 && (blockCommentIdx == -1 || (blockCommentIdx != -1 && blockCommentIdx > singleCommentIdx))){  
                    NumOfSingleCommentLines++;
                    NumOfCommentLines++;

                    // skip to the next line if it starts with single comment
                    continue;
                } 
                
                if (blockCommentIdx > -1){
                    NumOfBlockCommentLines++;
                    NumOfCommentLines++;
                    isBlockComment = true;
                    endBlockCommentIdx = line.indexOf(endBlockComment,blockCommentIdx + blockComment.length());
                }

                // if the block comment end, check if has more comments

                do {
                    if (endBlockCommentIdx > -1){
                        NumOfBlockComments++;
                        isBlockComment = false;
                        int newStartIdx = endBlockCommentIdx + endBlockComment.length();
                        singleCommentIdx = line.indexOf(comment, newStartIdx);

                        // if single comment starts after block comment end, then break
                        if (singleCommentIdx > -1) {  
                            NumOfSingleCommentLines++;
                            break;
                        }
                        blockCommentIdx = line.indexOf(blockComment, newStartIdx);
                        endBlockCommentIdx = line.indexOf(endBlockComment, blockCommentIdx + blockComment.length());
                    } else {
                        blockCommentIdx = -1;
                    }
                }  while (blockCommentIdx > -1);
                
            }

            // output
            System.out.println("Total # of lines: " + NumOfLines);
            System.out.println("Total # of comment lines: " + NumOfCommentLines);
            System.out.println("Total # of single line comments: " + NumOfSingleCommentLines);
            System.out.println("Total # of comment lines within block comments: " + NumOfBlockCommentLines);
            System.out.println("Total # of block line comments: " + NumOfBlockComments);
            System.out.println("Total # of TODO's: " + NumOfTodos);

            // close file
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Cannot Find file '" + fileName + "'");       
        }
        catch(IOException ex) {
            System.out.println("Error occured when reading the file '"  + fileName + "'");
        }
	}
}


