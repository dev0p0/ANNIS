/*
 * Copyright 2015 Corpuslinguistic working group Humboldt University Berlin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package annis.gui.exporter;

import static annis.model.AnnisConstants.ANNIS_NS;
import static annis.model.AnnisConstants.FEAT_MATCHEDIDS;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SFeature;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;

import com.google.common.base.Joiner;

import annis.CommonHelper;
import annis.model.AnnisConstants;
import annis.service.objects.Match;
import annis.service.objects.SubgraphFilter;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * An exporter that will take all token nodes and exports
 * them in a kind of grid.
 * This is useful for getting references of texts where the normal token based
 * text exporter doesn't work since there are multiple speakers or normalizations.
 * 
 * @author Thomas Krause <krauseto@hu-berlin.de>
 * @author irina
 */
@PluginImplementation
public class MatchWithContextExporterDev extends SaltBasedExporter
{
	private static final String TRAV_IS_DOMINATED_BY_MATCH = "IsDominatedByMatch";
	private static final String TRAV_SPEAKER_HAS_MATCHES = "SpeakerHasMatches";
	private static HashMap <String, Boolean> speakerHasMatches = new HashMap<String, Boolean>();
	private static String speakerName;
	private boolean isFirstSpeakerWithMatch = true;   
	private static long maxHeight;
	private static int currHeight;
	private static List <Long> dominatedMatchedCodes = new ArrayList<Long>();
	private static Map <Integer, List<Long>> dominanceLists = new HashMap <Integer, List<Long>>();
	private static Map <Long, List<Long>> dominanceListsWithHead = new HashMap <Long, List<Long>>();
	private static Set<Long> inDominanceRelation = new HashSet<Long>();
	private static Map <Integer, List<Long>> dominanceListsAllToken = new HashMap <Integer, List<Long>>();
  
	
  private static class IsDominatedByMatch implements GraphTraverseHandler
  {
   
    Long matchedNode = null;
    

    @Override
    public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
        SRelation<SNode, SNode> relation, SNode fromNode, long order)
    {
    	 SFeature matchedAnno = currNode.getFeature(AnnisConstants.ANNIS_NS, AnnisConstants.FEAT_MATCHEDNODE);
    	 
    	 if (traversalId.equals(TRAV_SPEAKER_HAS_MATCHES))
	        {
	    	  if (relation!= null)
				{			
						currHeight++;
						if (maxHeight < currHeight)
						{
							maxHeight = currHeight;
						}						
				}	
			    	  
	        } 
    	 
    	 if(matchedAnno != null)
	      {
	        matchedNode = matchedAnno.getValue_SNUMERIC();	       
	        
	        if (traversalId.equals(TRAV_SPEAKER_HAS_MATCHES))
	        {
	        dominatedMatchedCodes.add(matchedNode);
	        
	        if (dominatedMatchedCodes.size() > 1){
	        	inDominanceRelation.add(matchedNode);
	        }
	       
	        speakerHasMatches.put(speakerName, true);
	        }
	      }
	      
	     
	      
	      
    }

    @Override
    public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
        SRelation<SNode, SNode> relation, SNode fromNode, long order)
    {
    	 if (traversalId.equals(TRAV_SPEAKER_HAS_MATCHES))
	        {
    		 if (relation!= null)
	    		 {
	    		 currHeight--;	  
		        }
	        }
     
    }

    @Override
    public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
        SRelation relation, SNode currNode, long order)
    {
    	if(traversalId.equals(TRAV_IS_DOMINATED_BY_MATCH))
    	{	
	      if(this.matchedNode != null)
	      { // don't traverse any further if matched node was found 
	        return false;
	      }
	      else
	      {
	        // only iterate over text-coverage relations
	        return 
	            relation == null
	            || relation instanceof SDominanceRelation 
	            || relation instanceof SSpanningRelation;
	      }
    	}
    	else if (traversalId.equals(TRAV_SPEAKER_HAS_MATCHES)){
    		return 
    	            relation == null
    	            || relation instanceof SDominanceRelation 
    	            || relation instanceof SSpanningRelation;
    	}
    	else{
    		return true;
    	}
    } 
  }

  
  @Override
  public void convertText(SDocumentGraph graph, List<String> annoKeys,
    Map<String, String> args, int matchNumber, Writer out)
    throws IOException
  { 
  	String currSpeakerName = "";
	String prevSpeakerName = "";
	
		
	    
    if(graph != null)
    {
      List<SToken> orderedToken = graph.getSortedTokenByText();
      
      if(orderedToken != null)
      {
    	  for(SToken token : orderedToken){
    		  System.out.print(graph.getText(token) + "\t");
    	  }
    	  System.out.println("\n");
      }
      
      
      if(orderedToken != null)
      {    	   
    	 //reset the data structures for new graph
    	  speakerHasMatches.clear();    	  
    	  inDominanceRelation.clear();    	  
    	  dominanceLists.clear();    	  
    	  dominanceListsWithHead.clear();
    	 // counter over dominance lists
    	  int counter = 0;
    
    	// iterate first time over tokens to figure out which speaker has matches and to recognize the hierarchical structure of matches as well
    	  for(SToken token : orderedToken){
    		  counter++;
    		  System.out.println(counter + ". Token:\t" + graph.getText(token));
              maxHeight = 0;
              currHeight = 0;
             
              
    		  
              STextualDS textualDS = CommonHelper.getTextualDSForNode(token, graph);
             // System.out.println(textualDS.getName() + "\t" + textualDS.getText());
              speakerName = textualDS.getName();
              
              if (!speakerHasMatches.containsKey(speakerName))
              {
            	  speakerHasMatches.put(speakerName, false);
              }
              else if (speakerHasMatches.get(speakerName) == true)
              {
            	  //TODO
            	 // continue;
              }
        
              
    		  List<SNode> root = new LinkedList<>();
              root.add(token);
              IsDominatedByMatch traverserSpeakerSearch = new IsDominatedByMatch();
              
                          
              //reset list
        	  dominatedMatchedCodes = new ArrayList<Long>();
        	  
              
              graph.traverse(root, GRAPH_TRAVERSE_TYPE.BOTTOM_UP_DEPTH_FIRST, TRAV_SPEAKER_HAS_MATCHES, traverserSpeakerSearch); 
              
              
              if (!dominatedMatchedCodes.isEmpty()){
            	  dominanceListsWithHead.put(dominatedMatchedCodes.get(0), dominatedMatchedCodes);
                  dominanceLists.put(counter, dominatedMatchedCodes);
              }
                                      
    	  }
    	  
     	  
    	 //iterate again 
        ListIterator<SToken> it = orderedToken.listIterator();
        long lastTokenWasMatched = -1;
        boolean noPreviousTokenInLine = false;
       
        
        Iterator<Long> inDomIt = inDominanceRelation.iterator();        
        //eliminate entries whose key (matching code) dominate other matching codes  
        while(inDomIt.hasNext()){
        	Long matchingCode = inDomIt.next();
        	if (dominanceListsWithHead.containsKey(matchingCode)){
        		dominanceListsWithHead.remove(matchingCode);
        	}
        }
        
        Set<Map.Entry<Integer, List<Long>>> entries = dominanceLists.entrySet();
        // a helping data structure to eliminate duplicates of dominance lists
        Map <Integer, List<Long>> dominanceListsWithoutDoubles = new HashMap<Integer, List<Long>>();
        
        for(Map.Entry<Integer, List<Long>> entry : entries){
        	if (dominanceListsWithHead.containsValue(entry.getValue())){
        		dominanceListsWithoutDoubles.put(entry.getKey(), entry.getValue());
        	}
        }
        
                
        System.out.println(dominanceLists);
        System.out.println(dominanceListsWithHead);
        System.out.println(dominanceListsWithoutDoubles);
        
               
        // create adjacency matrix for storing of max distances between matching codes
        List <Map.Entry<Integer, List<Long>>> domListsSorted =  sortByKey(dominanceListsWithoutDoubles);
        int elementCount = 0;
        for ( Map.Entry <Integer, List<Long>> entry : domListsSorted){
			 elementCount += entry.getValue().size();
        }
        
        // TODO this approach works only if sorted match codes are a sequence 1,2,3, ... 
        int [][] adjacencyMatrix = new int [elementCount][elementCount];
        Map <Long, Integer> maxDistances = new HashMap<Long, Integer>();
        // set initial values
        for (int i=0; i < elementCount; i++){
        	for (int j=0; j < elementCount; j++){
        		adjacencyMatrix[i][j] = -1;
        	}
        }
        
        
        for ( Map.Entry <Integer, List<Long>> entry : domListsSorted){
			 List<Long> doms = entry.getValue();
			for (int i = 0; i< doms.size(); i++){
				int maxDist = 0;
						
				for (int j = 0; j < doms.size(); j++){
					int dist =  Math.abs(i - j);
					adjacencyMatrix[Integer.valueOf(String.valueOf(doms.get(i))) - 1][Integer.valueOf(String.valueOf(doms.get(j))) - 1] = dist;
					if (maxDist < dist){
						maxDist = dist;						
					}
					if (doms.get(i) > doms.get(j)){
						maxDistances.put(doms.get(i) - 1, maxDist);
					}
					
				}
			}
       }
       
        /*for (int i = 0; i < adjacencyMatrix.length; i++){
        	for (int j = 0; j < adjacencyMatrix[0].length; j++){
        		System.out.print(adjacencyMatrix[i][j] + "\t");
        	}
        	System.out.print("\n");
        }        
        System.out.println("maxDist: " +maxDistances);
        */
        
        
        
        
      //TODO why does match number start with -1? 
    	//if match number == -1, reset global variables 
    	if (matchNumber == -1){
    		isFirstSpeakerWithMatch = true;
    	}
    	
         
    	 //reset counter
        counter = 0;
        while(it.hasNext())
        {    	
          SToken tok = it.next();    
          counter++;
          //get current speaker name
          currSpeakerName = CommonHelper.getTextualDSForNode(tok, graph).getName();
                    
          
          // if speaker has no matches, skip token
          if (speakerHasMatches.get(currSpeakerName) == false)
          {
        	  prevSpeakerName = currSpeakerName;
        	 // continue;
          }
          
          //if speaker has matches
          else
          {			
        	  
	        	  //if the current speaker is new, append his name and write header
	        	 if (!currSpeakerName.equals(prevSpeakerName))
	        	 { 
	   
	        		 if (isFirstSpeakerWithMatch){
	        			 
	        			 out.append("match_number\t");
	        			 out.append("speaker\t");
	        			 
	        			 out.append("left_context\t");
		        		 
		        		 String prefix = "M_";
		        		 
		        		 System.out.println("dominanceListsWithoutDoubles: " +  dominanceListsWithoutDoubles);
		        		 
		        		 domListsSorted =  sortByKey(dominanceListsWithoutDoubles);
		        		 int listCount = domListsSorted.size();
		        		 int count = 0;
		        		 
		        		 for ( Map.Entry <Integer, List<Long>> entry : domListsSorted){
		        			 count++;
		        			 List <Long> dominanceChain = entry.getValue();
		        			 
		        			 for ( int i = dominanceChain.size() - 1; i >= 0; i--){
		        				 out.append(prefix + dominanceChain.get(i) + "\t");
		        			 }
		        			 
		        			 if (dominanceChain.size() > 1){
		        				 for ( int i = 1; i < dominanceChain.size(); i++){
			        				 out.append(prefix + dominanceChain.get(i) + "\t");
			        			 }
		        			 }
		        			 
		        			 if (count < listCount){
		        				 out.append("middle_context_" +  count + "\t"); 
		        			 }
		        			 
		        			 
		        		 }
		        		 
		        		 out.append("right_context");
		        		 out.append("\n");
	        			 
	        			 isFirstSpeakerWithMatch = false;
	        		 }
	        		 else {
	        			 out.append("\n");
	        		 } 
	            		   		
	        		 	        		
	        		 // TODO why does matchNumber start with -1?
	        		 out.append(String.valueOf(matchNumber + 2) + "\t");
	        		 out.append(currSpeakerName + "\t");
	        		 
	        		 
	        		 lastTokenWasMatched = -1;
	        		 noPreviousTokenInLine = true;
	        		 
	        		
	        	 }// header ready
	        	 
	        	  String separator = " "; // default to space as separator
	        	       	  
	        	  		  List<SNode> root = new LinkedList<>();
		                  root.add(tok);
		                  IsDominatedByMatch traverser = new IsDominatedByMatch();
		                  graph.traverse(root, GRAPH_TRAVERSE_TYPE.BOTTOM_UP_DEPTH_FIRST, "IsDominatedByMatch", traverser);
		                  if(traverser.matchedNode != null)
		                  {
		                    // is dominated by a (new) matched node, thus use tab to separate the non-matches from the matches
		                    if(lastTokenWasMatched < 0)
		                    {
		                      //separator = "\t"; 
		                      separator= "";
		                      if (maxDistances.containsKey(traverser.matchedNode -1)){
		                    	  Integer tabCount = maxDistances.get(traverser.matchedNode -1) + 1;			                    
			                    	  for (int i = 0; i < tabCount; i++){
				                    	  separator += "\t";
				                      }                     
		                      }
		                      else{
		                    	  separator = "\t";  
		                      }
		                      
		                			                      
		                    }
		                    else if(lastTokenWasMatched != (long) traverser.matchedNode)
		                    {
		                      // always leave an empty column between two matches, even if there is no actual context
		                      // separator = "\t\t";
		                    	separator= "";
		                    	int tabCount = adjacencyMatrix[(int) (lastTokenWasMatched - 1)][(int) ((long) traverser.matchedNode - 1)];
		                    	if (tabCount != -1){
		                    		for (int i = 0; i < tabCount; i++){
				                    	  separator += "\t";
				                      }
		                    	}
		                    	else{		                    		
		                    			separator = "\t";
		                    			                    		
		                    	}
		                    	
		                    }
		                    lastTokenWasMatched = traverser.matchedNode;
		                  }
		                  else if(lastTokenWasMatched >= 0)
		                  {
		                    // also mark the end of a match with the tab
		                    //separator = "\t";
		                	  if(graph.getText(tok).equals(",") && !dominanceLists.containsKey(counter) && 
		                			  dominanceLists.containsKey(counter-1) && dominanceLists.containsKey(counter+1)){
		                		    
		                		  
	                    			if (dominanceLists.get(counter-1).size() > dominanceLists.get(counter+1).size() &&
	                    					dominanceLists.get(counter-1).contains(dominanceLists.get(counter+1).get(0))){
	                    				separator = "";
	                    				//
	                    				System.out.println(counter + ": " + graph.getText(tok) + " -- " +lastTokenWasMatched);
	                    				int tabCount = adjacencyMatrix[(int) (dominanceLists.get(counter+1).get(0)- 1)][(int) lastTokenWasMatched -1];
	    		                    	if (tabCount != -1){
	    		                    		for (int i = 0; i < tabCount; i++){
	    				                    	  separator += "\t";
	    				                      }
	    		                    	}
	  			                      
	  			                      else{
	  			                    	  separator = "\t";
	  			                      }
	    		                    	lastTokenWasMatched = dominanceLists.get(counter+1).get(0);
	                    			}
                    				else if (dominanceLists.get(counter-1).size() <= dominanceLists.get(counter+1).size() &&
	                    					dominanceLists.get(counter+1).contains(dominanceLists.get(counter-1).get(0))){
                    					separator = " ";
                    				}
                    				else{
                    					// TODO this part identical with next one 
                    					separator= "";
       			                	  
       			                	  if (maxDistances.containsKey(lastTokenWasMatched - 1)){
       			                		  Integer tabCount = maxDistances.get(lastTokenWasMatched - 1) + 1;			                     
       				                    	  for (int i = 0; i < tabCount; i++){
       					                    	  separator += "\t";
       					                      }			                      
       			                	  }
       			                      
       			                      else{
       			                    	  separator = "\t";
       			                      }
       				                      
       			                	  
       			                	  
       			                    lastTokenWasMatched = -1;
                    				}
	                    				
	                    				
	                    			}
	                    		
		                	  else{ // if no comma handling necessary, only this part remains
		                		  separator= "";
			                	  
			                	  if (maxDistances.containsKey(lastTokenWasMatched - 1)){
			                		  Integer tabCount = maxDistances.get(lastTokenWasMatched - 1) + 1;			                     
				                    	  for (int i = 0; i < tabCount; i++){
					                    	  separator += "\t";
					                      }			                      
			                	  }
			                      
			                      else{
			                    	  separator = "\t";
			                      }
				                      
			                	  
			                	  
			                    lastTokenWasMatched = -1;
		                	  }
	                    				 
	                
		                	  
		                	 
		                  }
		                  
		                  //if tok is the first token in the line and not matched, set separator to empty string
		                  if (noPreviousTokenInLine && separator.equals(" "))
		                  {
		                	 separator = "";
		                  }
		                  out.append(separator);
		           
		       	          
		          
          // append the actual token
          out.append(graph.getText(tok));
          noPreviousTokenInLine = false; 
          prevSpeakerName = currSpeakerName;
               
         }              
          
        }
      
      }
       
    }

  }
  

  @Override
  public SubgraphFilter getSubgraphFilter()
  {
    return SubgraphFilter.all;
  }
  
  @Override
  public String getHelpMessage()
  {
    return null;
  }
  
  @Override
  public String getFileEnding()
  {
    return "csv";
  }
  
  private static <K, V> List<Map.Entry<K, V>> sortByKey(Map<K, V> map) {
		List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(map.size());

		for (Map.Entry<K, V> e : map.entrySet()) {
			entries.add(e);
		}

		Comparator<Map.Entry<K, V>> comparator = new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				return e1.getKey().toString().compareToIgnoreCase(e2.getKey().toString());

			}
		};
		// sort key values lexicographically
		Collections.sort(entries, comparator);

		return entries;
	}
  
}
