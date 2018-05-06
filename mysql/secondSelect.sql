SELECT crp,jmeno,najetoKm 
FROM(
	SELECT crp_ridic,(MAX(najeto) - MIN(najeto)) as najetoKm
	FROM rdb.mysqlProjeti
	WHERE cas BETWEEN TIMESTAMP('2018-03-26 06:30:00.0') AND TIMESTAMP('2018-04-23 23:59:59.0')
	AND crp_ridic NOT IN (
		SELECT rdb.mysqlProjeti.crp_ridic
		FROM rdb.mysqlProjeti INNER JOIN rdb.brana ON rdb.mysqlProjeti.id_brana = rdb.brana.id
		WHERE rdb.brana.typ='Satellite' AND cas BETWEEN TIMESTAMP('2018-03-26 06:30:00.0') AND TIMESTAMP('2018-04-23 23:59:59.0')
	)
	GROUP BY crp_ridic) AS A INNER JOIN ridic ON A.crp_ridic = ridic.crp
WHERE A.najetoKm > 10