SELECT allow
FROM ip_filter_rule
WHERE ((source_start_ip IS NOT NULL AND source_end_ip IS NOT NULL
    AND inet '%s' >= source_start_ip::inet AND inet '%s' <= source_end_ip::inet)
    OR source_subnet_ip IS NOT NULL AND (source_subnet_ip::cidr & '%s'::inet) = (source_subnet_ip::cidr)
    )
  AND
    ((destination_start_ip IS NOT NULL AND destination_end_ip IS NOT NULL
        AND inet '%s' >= destination_start_ip::inet AND inet '%s'  <= destination_end_ip::inet)
        OR destination_subnet_ip IS NOT NULL
         AND (destination_subnet_ip::cidr & '%s'::inet) = (destination_subnet_ip::cidr)
        )