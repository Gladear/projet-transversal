--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1 (Debian 12.1-1.pgdg100+1)
-- Dumped by pg_dump version 12.1 (Debian 12.1-1.pgdg100+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: intervention; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.intervention (
    id integer NOT NULL,
    sensor_id smallint NOT NULL,
    truck_id smallint NOT NULL,
    beginning date NOT NULL,
    ending date
);


ALTER TABLE public.intervention OWNER TO postgres;

--
-- Name: intervention_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.intervention_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.intervention_id_seq OWNER TO postgres;

--
-- Name: intervention_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.intervention_id_seq OWNED BY public.intervention.id;


--
-- Name: sensor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sensor (
    id smallint NOT NULL,
    label character varying NOT NULL,
    lat numeric(10,6) NOT NULL,
    lon numeric(10,6) NOT NULL
);


ALTER TABLE public.sensor OWNER TO postgres;

--
-- Name: sensor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sensor_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sensor_id_seq OWNER TO postgres;

--
-- Name: sensor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sensor_id_seq OWNED BY public.sensor.id;


--
-- Name: station; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.station (
    id smallint NOT NULL,
    lat numeric(10,6) NOT NULL,
    lon numeric(10,6) NOT NULL
);


ALTER TABLE public.station OWNER TO postgres;

--
-- Name: station_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.station_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.station_id_seq OWNER TO postgres;

--
-- Name: station_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.station_id_seq OWNED BY public.station.id;


--
-- Name: truck; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.truck (
    id smallint NOT NULL,
    station_id smallint NOT NULL
);


ALTER TABLE public.truck OWNER TO postgres;

--
-- Name: truck_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.truck_id_seq
    AS smallint
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.truck_id_seq OWNER TO postgres;

--
-- Name: truck_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.truck_id_seq OWNED BY public.truck.id;


--
-- Name: intervention id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.intervention ALTER COLUMN id SET DEFAULT nextval('public.intervention_id_seq'::regclass);


--
-- Name: sensor id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor ALTER COLUMN id SET DEFAULT nextval('public.sensor_id_seq'::regclass);


--
-- Name: station id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station ALTER COLUMN id SET DEFAULT nextval('public.station_id_seq'::regclass);


--
-- Name: truck id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.truck ALTER COLUMN id SET DEFAULT nextval('public.truck_id_seq'::regclass);


--
-- Data for Name: intervention; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.intervention (id, sensor_id, truck_id, beginning, ending) FROM stdin;
\.


--
-- Data for Name: sensor; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sensor (id, label, lat, lon) FROM stdin;
1	INSA de Lyon	45.783222	4.878086
2	Bibliothèque Marie Curie	45.782478	4.876638
3	La Doua - Gaston Berger	45.781472	4.872073
4	BU Sciences La Doua	45.782033	4.870421
5	La Rotonde (INSA de Lyon)	45.783810	4.874069
6	Restaurant Universitaire de Jussieux	45.780720	4.876386
7	Insa - Einstein	45.782351	4.877609
8	Croix Luizet	45.783644	4.883100
9	Résidence B - INSA Lyon	45.784680	4.883872
10	IUT Feyssine	45.786838	4.881952
11	Département Génie Civil - IUT Lyon 1	45.786789	4.884334
12	Nécropole Nationale de la Doua	45.785382	4.885927
13	Place de Croix Luizet	45.780875	4.883512
14	Rue du Canada	45.782211	4.880744
15	CNRS Délégation Rhône Auvergne	45.780335	4.874846
16	Quai 43	45.780219	4.871780
17	CPE Lyon	45.783788	4.868905
\.


--
-- Data for Name: station; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.station (id, lat, lon) FROM stdin;
1	45.762721	4.843915
2	45.778933	4.878044
3	45.765261	4.905551
4	45.749866	4.848206
5	45.746807	4.825719
6	45.731700	4.828598
\.


--
-- Data for Name: truck; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.truck (id, station_id) FROM stdin;
1	1
2	1
3	2
4	3
5	4
6	5
7	5
8	6
\.


--
-- Name: intervention_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.intervention_id_seq', 6, true);


--
-- Name: sensor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sensor_id_seq', 17, true);


--
-- Name: station_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.station_id_seq', 6, true);


--
-- Name: truck_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.truck_id_seq', 8, true);


--
-- Name: intervention intervention_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.intervention
    ADD CONSTRAINT intervention_pk PRIMARY KEY (id);


--
-- Name: intervention intervention_uq_start; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.intervention
    ADD CONSTRAINT intervention_uq_start UNIQUE (truck_id, beginning);


--
-- Name: sensor sensor_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT sensor_pk PRIMARY KEY (id);


--
-- Name: sensor sensor_uq_geo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT sensor_uq_geo UNIQUE (lat, lon);


--
-- Name: sensor sensor_uq_label; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT sensor_uq_label UNIQUE (label);


--
-- Name: station station_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station
    ADD CONSTRAINT station_pk PRIMARY KEY (id);


--
-- Name: station station_uq_geo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.station
    ADD CONSTRAINT station_uq_geo UNIQUE (lat, lon);


--
-- Name: truck truck_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.truck
    ADD CONSTRAINT truck_pk PRIMARY KEY (id);


--
-- Name: idx_intervention_sensor; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_intervention_sensor ON public.intervention USING btree (sensor_id);


--
-- Name: idx_intervention_truck; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_intervention_truck ON public.intervention USING btree (truck_id);


--
-- Name: idx_truck_station; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_truck_station ON public.truck USING btree (station_id);


--
-- Name: intervention intervention_sensor_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.intervention
    ADD CONSTRAINT intervention_sensor_fk FOREIGN KEY (sensor_id) REFERENCES public.sensor(id);


--
-- Name: intervention intervention_truck_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.intervention
    ADD CONSTRAINT intervention_truck_fk FOREIGN KEY (truck_id) REFERENCES public.truck(id);


--
-- Name: truck truck_station_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.truck
    ADD CONSTRAINT truck_station_fk FOREIGN KEY (station_id) REFERENCES public.station(id);


--
-- PostgreSQL database dump complete
--

