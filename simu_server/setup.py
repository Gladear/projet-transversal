from setuptools import setup

setup(
    name='simu_server',
    packages=['simu_server'],
    include_package_data=True,
    install_requires=[
        'flask',
    ],
)