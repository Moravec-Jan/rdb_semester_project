SELECT crp,jmeno,najetoKm 
FROM(
	SELECT crp_ridic,(MAX(najeto) - MIN(najeto)) as najetoKm
	FROM rdb.projeti
	WHERE cas BETWEEN TIMESTAMP('2018-4-23') AND TIMESTAMP('2018-4-23 23:59:59')
	AND crp_ridic NOT IN (
		SELECT rdb.projeti.crp_ridic
		FROM rdb.projeti INNER JOIN rdb.brana ON rdb.projeti.id_brana = rdb.brana.id 
		WHERE rdb.brana.typ="Satellite" AND cas BETWEEN TIMESTAMP('2018-4-23') AND TIMESTAMP('2018-4-23 23:59:59')
	)
	GROUP BY crp_ridic) AS A INNER JOIN ridic ON A.crp_ridic = ridic.crp
WHERE A.najetoKm > 50