/*
 * Copyright 2009-2011 Collaborative Research Centre SFB 632 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package annis.sqlgen;

import annis.model.QueryNode;
import annis.ql.parser.QueryData;
import annis.service.objects.MatchAndDocumentCount;
import static annis.sqlgen.AbstractSqlGenerator.TABSTOP;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DataAccessException;


public class CountMatchesAndDocumentsSqlGenerator extends AbstractSolutionMatchInFromClauseSqlGenerator
	implements SelectClauseSqlGenerator<QueryData>, FromClauseSqlGenerator<QueryData>, 
     SqlGeneratorAndExtractor<QueryData, MatchAndDocumentCount>
{

	@Override
	public String selectClause(QueryData queryData, List<QueryNode> alternative, String indent) {
		return "\n" + indent + TABSTOP + "count(*) AS tupleCount, count(distinct corpus_ref) AS docCount";
	}

	@Override
	public MatchAndDocumentCount extractData(ResultSet rs) throws SQLException, DataAccessException {
    
    MatchAndDocumentCount c = new MatchAndDocumentCount();
    
    int tupleSum = 0;
    int docSum = 0;
		while (rs.next())
    {
			tupleSum += rs.getInt("tupleCount");
      docSum += rs.getInt("docCount");
    }
    c.setMatchCount(tupleSum);
    c.setDocumentCount(docSum);
    
		return c;	
	}
}