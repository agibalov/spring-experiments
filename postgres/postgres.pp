class { 'postgresql::server': 
  listen_addresses => '*',
  ip_mask_deny_postgres_user => '0.0.0.0/32',
  ip_mask_allow_all_users => '0.0.0.0/0',
  postgres_password => 'qwerty'
}

postgresql::server::db { 'testdb':
  user => 'postgres',
  password => postgresql_password('postgres', 'qwerty')
}

